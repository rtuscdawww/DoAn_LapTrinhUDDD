package com.vanlang.myvlufindapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vanlang.myvlufindapp.R
import com.vanlang.myvlufindapp.data.repository.AuthRepository
import com.vanlang.myvlufindapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render()
    }

    private fun render() {
        val user = authRepository.currentUser
        if (user == null) {
            binding.textProfileInfo.setText(R.string.profile_not_logged_in)
            binding.buttonProfileAction.setText(R.string.login_button)
            binding.buttonProfileAction.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
        } else {
            binding.textProfileInfo.text = getString(R.string.profile_logged_in_as, user.email)
            binding.buttonProfileAction.setText(R.string.logout_button)
            binding.buttonProfileAction.setOnClickListener {
                authRepository.logout()
                render()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}