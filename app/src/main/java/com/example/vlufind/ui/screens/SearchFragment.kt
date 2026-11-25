package com.example.vlufind.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vlufind.R
import com.example.vlufind.adapter.PostAdapter
import com.example.vlufind.databinding.FragmentSearchBinding
import com.example.vlufind.model.LostFoundPost
import com.example.vlufind.model.PostType

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter
    private var postList = listOf<LostFoundPost>()
    private var selectedType: PostType? = null
    private var keyword = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postAdapter = PostAdapter(emptyList()) { post ->
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    PostDetailFragment.newInstance(post)
                )
                .addToBackStack(null)
                .commit()
        }

        binding.rvSearchResults.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvSearchResults.adapter = postAdapter

        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    keyword = query.orEmpty()
                    filterPosts()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    keyword = newText.orEmpty()
                    filterPosts()
                    return true
                }
            }
        )

        binding.radioGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            selectedType = when (checkedId) {
                R.id.rbLost -> PostType.LOST
                R.id.rbFound -> PostType.FOUND
                else -> null
            }

            filterPosts()
        }

        filterPosts()
    }

    private fun filterPosts() {
        val filteredList = postList.filter { post ->

            val matchesKeyword =
                post.title.contains(keyword.trim(), ignoreCase = true) ||
                        post.description.contains(keyword.trim(), ignoreCase = true) ||
                        post.location.contains(keyword.trim(), ignoreCase = true)

            val matchesType =
                selectedType == null || post.type == selectedType

            matchesKeyword && matchesType
        }

        postAdapter.updateData(filteredList)

        binding.tvResultCount.text =
            "Tìm thấy ${filteredList.size} kết quả"

        binding.tvEmpty.visibility =
            if (filteredList.isEmpty()) View.VISIBLE else View.GONE

        binding.rvSearchResults.visibility =
            if (filteredList.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}