package com.vanlang.myvlufindapp.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vanlang.myvlufindapp.R
import com.vanlang.myvlufindapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.buttonRegister.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString()
            val confirm = binding.editConfirm.text.toString()

            binding.layoutEmail.error = null
            binding.layoutPassword.error = null
            binding.layoutConfirm.error = null
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.layoutEmail.error = getString(R.string.error_invalid_email)
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.layoutPassword.error = getString(R.string.error_short_password)
                return@setOnClickListener
            }
            if (password != confirm) {
                binding.layoutConfirm.error = getString(R.string.error_password_mismatch)
                return@setOnClickListener
            }
            viewModel.register(email, password)
        }

        binding.textGoLogin.setOnClickListener { findNavController().popBackStack() }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressRegister.visibility = if (loading) View.VISIBLE else View.GONE
            binding.buttonRegister.isEnabled = !loading
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
        viewModel.success.observe(viewLifecycleOwner) { success ->
            // Đăng ký thành công là đã đăng nhập: bỏ cả hai màn hình đăng nhập/đăng ký
            if (success) findNavController().popBackStack(R.id.loginFragment, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}