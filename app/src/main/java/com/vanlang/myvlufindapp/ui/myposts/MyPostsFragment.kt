package com.example.vlufind.ui.screens

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vlufind.R
import com.example.vlufind.adapter.PostAdapter
import com.example.vlufind.data.MockData
import com.example.vlufind.data.FavoriteManager
import com.example.vlufind.databinding.FragmentMyPostsBinding
import com.example.vlufind.model.LostFoundPost

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter

    private var myPosts = emptyList<LostFoundPost>()

    private val redColor = Color.parseColor("#B71C1C")
    private val grayColor = Color.parseColor("#757575")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyPostsBinding.inflate(
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

        postAdapter = PostAdapter(emptyList()) { post ->

            val detailFragment =
                PostDetailFragment.newInstance(post)

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    detailFragment
                )
                .addToBackStack(null)
                .commit()
        }

        binding.rvMyPosts.apply {

            layoutManager = LinearLayoutManager(
                requireContext()
            )

            adapter = postAdapter
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnDeletePost.setOnClickListener {
            showDeletePostDialog()
        }

        loadMyPosts()
    }

    private fun getCurrentEmail(): String {

        return requireActivity()
            .getSharedPreferences(
                "LoginPrefs",
                Context.MODE_PRIVATE
            )
            .getString("CURRENT_EMAIL", "")
            .orEmpty()
    }

    private fun getCurrentUserId(): Int {

        return getCurrentEmail()
            .lowercase()
            .hashCode()
    }

    private fun loadMyPosts() {

        val email = getCurrentEmail()
        val userId = getCurrentUserId()

        myPosts = if (email.isBlank()) {

            emptyList()

        } else {

            MockData.posts.filter {
                it.postedBy.id == userId
            }
        }

        binding.tvMyPostCount.text =
            "${myPosts.size} bài đăng"

        postAdapter.updateData(myPosts)

        if (myPosts.isEmpty()) {

            binding.layoutEmpty.visibility = View.VISIBLE

            binding.rvMyPosts.visibility = View.GONE

            binding.btnDeletePost.visibility = View.GONE

        } else {

            binding.layoutEmpty.visibility = View.GONE

            binding.rvMyPosts.visibility = View.VISIBLE

            binding.btnDeletePost.visibility = View.VISIBLE
        }
    }

    private fun showDeletePostDialog() {

        if (myPosts.isEmpty()) {
            return
        }

        val postsToDelete = myPosts.toList()

        val postTitles = postsToDelete.map {
            it.title
        }.toTypedArray()

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Chọn bài đăng cần xóa")
            .setItems(postTitles) { _, position ->

                val selectedPost = postsToDelete[position]

                confirmDeletePost(selectedPost)
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(grayColor)
    }

    private fun confirmDeletePost(post: LostFoundPost) {

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Xóa bài đăng")
            .setMessage(
                "Bạn có chắc chắn muốn xóa bài đăng " +
                        "\"${post.title}\" không?\n\n" +
                        "Bài đăng đã xóa không thể khôi phục."
            )
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ ->

                deletePost(post)
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(grayColor)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(redColor)
    }

    private fun deletePost(post: LostFoundPost) {

        val context = requireContext()

        if (getCurrentEmail().isBlank()) {
            Toast.makeText(
                context,
                "Vui lòng đăng nhập!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val userId = getCurrentUserId()

        val deleted = MockData.deletePost(
            context,
            post.id,
            userId
        )

        if (deleted) {

            if (FavoriteManager.isFavorite(context, post.id)) {

                FavoriteManager.toggleFavorite(
                    context,
                    post.id
                )
            }

            Toast.makeText(
                context,
                "Đã xóa bài đăng thành công!",
                Toast.LENGTH_SHORT
            ).show()

            loadMyPosts()

        } else {

            Toast.makeText(
                context,
                "Không thể xóa bài đăng!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {

        super.onResume()

        if (_binding != null &&
            ::postAdapter.isInitialized
        ) {
            loadMyPosts()
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}