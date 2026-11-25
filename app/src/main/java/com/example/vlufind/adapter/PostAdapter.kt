package com.example.vlufind.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vlufind.databinding.ItemPostBinding
import com.example.vlufind.model.LostFoundPost
import com.example.vlufind.model.PostType

class PostAdapter(
    private var postList: List<LostFoundPost>,
    private val onPostClick: (LostFoundPost) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(
        val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        val post = postList[position]
        val binding = holder.binding

        // Hiển thị thông tin bài đăng
        binding.tvPostTitle.text = post.title
        binding.tvPostDescription.text = post.description
        binding.tvPostLocation.text = "Địa điểm: ${post.location}"

        // Hiển thị loại tin
        if (post.type == PostType.LOST) {
            binding.tvPostType.text = "BỊ MẤT"
            binding.tvPostType.setTextColor(
                Color.parseColor("#B71C1C")
            )
        } else {
            binding.tvPostType.text = "NHẶT ĐƯỢC"
            binding.tvPostType.setTextColor(
                Color.parseColor("#2E7D32")
            )
        }

        // Nhấn vào item để mở màn hình chi tiết
        binding.root.setOnClickListener {
            onPostClick(post)
        }
    }

    override fun getItemCount(): Int = postList.size

    fun updateData(newList: List<LostFoundPost>) {
        postList = newList
        notifyDataSetChanged()
    }
}