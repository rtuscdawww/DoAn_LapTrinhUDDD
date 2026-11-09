package com.vanlang.myvlufindapp.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vanlang.myvlufindapp.R
import com.vanlang.myvlufindapp.data.repository.AuthRepository
import com.vanlang.myvlufindapp.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loggedIn = AuthRepository().currentUser != null
        binding.layoutLoginRequired.visibility = if (loggedIn) View.GONE else View.VISIBLE
        binding.textPostContent.visibility = if (loggedIn) View.VISIBLE else View.GONE
        binding.buttonGoLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}