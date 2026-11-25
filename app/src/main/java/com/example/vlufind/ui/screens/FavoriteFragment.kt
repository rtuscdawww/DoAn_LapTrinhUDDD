package com.example.vlufind.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vlufind.R
import com.example.vlufind.adapter.PostAdapter
import com.example.vlufind.data.FavoriteManager
import com.example.vlufind.data.MockData
import com.example.vlufind.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        postAdapter = PostAdapter(
            emptyList(),
            onFavoriteChanged = {
                loadFavorites()
            }
        ) { post ->

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    PostDetailFragment.newInstance(post)
                )
                .addToBackStack(null)
                .commit()
        }

        binding.rvFavorites.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvFavorites.adapter = postAdapter

        loadFavorites()
    }

    private fun loadFavorites() {

        val favoriteIds = FavoriteManager.getFavorites(
            requireContext()
        )

        val favoritePosts = MockData.posts.filter {
            favoriteIds.contains(it.id.toString())
        }

        postAdapter.updateData(favoritePosts)

        binding.tvFavoriteCount.text =
            "${favoritePosts.size} bài đăng yêu thích"

        binding.layoutEmpty.visibility =
            if (favoritePosts.isEmpty()) View.VISIBLE
            else View.GONE

        binding.rvFavorites.visibility =
            if (favoritePosts.isEmpty()) View.GONE
            else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        if (_binding != null && ::postAdapter.isInitialized) {
            loadFavorites()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}