package com.vanlang.myvlufindapp.ui.myposts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vanlang.myvlufindapp.R
import com.vanlang.myvlufindapp.data.repository.AuthRepository
import com.vanlang.myvlufindapp.databinding.FragmentMyPostsBinding
import com.vanlang.myvlufindapp.ui.home.PostAdapter

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = AuthRepository().currentUser?.email
        if (email == null) {
            findNavController().popBackStack()
            return
        }

        val adapter = PostAdapter { post ->
            findNavController().navigate(
                R.id.postDetailFragment,
                bundleOf("postId" to post.id)
            )
        }
        binding.recyclerMyPosts.adapter = adapter

        val viewModel = ViewModelProvider(this)[MyPostsViewModel::class.java]
        viewModel.getMyPosts(email).observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.textEmptyMyPosts.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerMyPosts.adapter = null
        _binding = null
    }
}