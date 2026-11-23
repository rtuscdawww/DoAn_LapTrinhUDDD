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
import com.vanlang.myvlufindapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.buttonLogin.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString()

            binding.layoutEmail.error = null
            binding.layoutPassword.error = null
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.layoutEmail.error = getString(R.string.error_invalid_email)
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.layoutPassword.error = getString(R.string.error_short_password)
                return@setOnClickListener
            }
            viewModel.login(email, password)
        }

        binding.textForgot.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.layoutEmail.error = getString(R.string.enter_email_first)
                return@setOnClickListener
            }
            binding.layoutEmail.error = null
            viewModel.resetPassword(email)
        }

        binding.textGoRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressLogin.visibility = if (loading) View.VISIBLE else View.GONE
            binding.buttonLogin.isEnabled = !loading
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
        viewModel.resetSent.observe(viewLifecycleOwner) { sent ->
            if (sent) Toast.makeText(requireContext(), R.string.reset_email_sent, Toast.LENGTH_LONG).show()
        }
        viewModel.success.observe(viewLifecycleOwner) { success ->
            // Đăng nhập xong thì quay lại màn hình trước đó (bài đăng hoặc tab đang dùng)
            if (success) findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}