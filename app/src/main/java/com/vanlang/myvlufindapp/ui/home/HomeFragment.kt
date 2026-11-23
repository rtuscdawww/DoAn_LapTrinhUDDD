package com.vanlang.myvlufindapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vanlang.myvlufindapp.databinding.FragmentHomeBinding
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.vanlang.myvlufindapp.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostAdapter { post ->
            findNavController().navigate(
                R.id.postDetailFragment,
                bundleOf("postId" to post.id)
            )
        }
        binding.recyclerPosts.adapter = adapter

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel.posts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerPosts.adapter = null
        _binding = null
    }
}