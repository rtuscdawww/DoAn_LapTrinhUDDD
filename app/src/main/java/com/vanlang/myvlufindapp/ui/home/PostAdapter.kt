package com.vanlang.myvlufindapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanlang.myvlufindapp.R
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.databinding.ItemPostBinding
import com.vanlang.myvlufindapp.ui.label

class PostAdapter(
    private val onItemClick: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback) {

    class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, onItemClick: (Post) -> Unit) {
            binding.textType.text = "${post.type.label()} • ${post.status.label()}"
            binding.textTitle.text = post.title
            binding.textLocation.text = post.location
            binding.textDescription.text = post.description

            Glide.with(binding.imagePost)
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .fallback(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(binding.imagePost)

            binding.root.setOnClickListener { onItemClick(post) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
        }
    }
}