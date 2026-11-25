package com.example.vlufind.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vlufind.adapter.PostAdapter
import com.example.vlufind.data.MockData
import com.example.vlufind.databinding.FragmentHomeBinding
import com.example.vlufind.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.Normalizer

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(
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

        val postList = MockData.posts

        postAdapter = PostAdapter(postList) { post ->

            val detailFragment = PostDetailFragment.newInstance(post)

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    detailFragment
                )
                .addToBackStack(null)
                .commit()
        }

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
        binding.fabPostItem.setOnClickListener {

            val bottomNav = requireActivity()
                .findViewById<BottomNavigationView>(
                    R.id.bottom_navigation
                )

            bottomNav.selectedItemId = R.id.nav_create_post
        }

        binding.etSearchBox.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    searchPosts(s?.toString().orEmpty())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        )
    }

    private fun searchPosts(keyword: String) {

        val searchText = normalizeText(keyword.trim())

        if (searchText.isEmpty()) {
            postAdapter.updateData(MockData.posts)
            return
        }

        val exactResults = MockData.posts.filter { post ->
            val title = normalizeText(post.title)

            Regex("\\b${Regex.escape(searchText)}\\b")
                .containsMatchIn(title)
        }

        val filteredList = if (exactResults.isNotEmpty()) {
            exactResults
        } else {
            MockData.posts.filter { post ->
                normalizeText(post.title)
                    .split(Regex("\\s+"))
                    .any { it.startsWith(searchText) }
            }
        }

        postAdapter.updateData(filteredList)
    }

    private fun normalizeText(text: String): String {

        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .lowercase()
            .replace("đ", "d")
    }
    override fun onResume() {
        super.onResume()

        val bottomNav = requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (bottomNav != null) {
            bottomNav.visibility = View.VISIBLE
        }

        if (_binding != null && ::postAdapter.isInitialized) {
            searchPosts(binding.etSearchBox.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}