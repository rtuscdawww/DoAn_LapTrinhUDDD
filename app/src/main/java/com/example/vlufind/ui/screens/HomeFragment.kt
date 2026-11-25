package com.example.vlufind.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vlufind.adapter.PostAdapter
import com.example.vlufind.data.MockData
import com.example.vlufind.databinding.FragmentHomeBinding
import com.example.vlufind.R

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Lấy danh sách bài đăng
        val postList = MockData.posts

        // 2. Khởi tạo Adapter và xử lý khi nhấn vào item
        postAdapter = PostAdapter(postList) { post ->

            // Mở màn hình chi tiết và truyền dữ liệu bài đăng
            val detailFragment = PostDetailFragment.newInstance(post)

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    detailFragment
                )
                .addToBackStack(null)
                .commit()
        }

        // 3. Cấu hình RecyclerView
        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}