package com.example.vlufind.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vlufind.R
import com.example.vlufind.data.FavoriteManager
import com.example.vlufind.databinding.ItemPostBinding
import com.example.vlufind.model.LostFoundPost
import com.example.vlufind.model.PostType

class PostAdapter(
    private var postList: List<LostFoundPost>,
    private val onFavoriteChanged: (() -> Unit)? = null,
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
        val context = binding.root.context

        binding.tvPostTitle.text = post.title
        binding.tvPostDescription.text = post.description
        binding.tvPostLocation.text = "Địa điểm: ${post.location}"

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

        updateFavoriteIcon(
            binding,
            FavoriteManager.isFavorite(context, post.id)
        )

        binding.btnFavorite.setOnClickListener {
            val isFavorite = FavoriteManager.toggleFavorite(
                context,
                post.id
            )

            updateFavoriteIcon(binding, isFavorite)

            onFavoriteChanged?.invoke()
        }

        binding.root.setOnClickListener {
            onPostClick(post)
        }
    }

    private fun updateFavoriteIcon(
        binding: ItemPostBinding,
        isFavorite: Boolean
    ) {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(
                R.drawable.ic_favorite
            )

            binding.btnFavorite.setColorFilter(
                Color.parseColor("#B71C1C")
            )

            binding.btnFavorite.contentDescription =
                "Bỏ yêu thích"
        } else {
            binding.btnFavorite.setImageResource(
                R.drawable.ic_favorite_border
            )

            binding.btnFavorite.setColorFilter(
                Color.parseColor("#757575")
            )

            binding.btnFavorite.contentDescription =
                "Yêu thích"
        }
    }

    override fun getItemCount(): Int = postList.size

    fun updateData(newList: List<LostFoundPost>) {
        postList = newList
        notifyDataSetChanged()
    }
}