package com.vanlang.myvlufindapp.data.remote

import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType

data class PostDto(
    val id: String? = null,
    val type: String? = null,
    val status: String? = null,
    val title: String? = null,
    val category: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val location: String? = null,
    val contact: String? = null,
    val authorName: String? = null,
    val hiddenDetail: String? = null,
    val ownerEmail: String? = null,
    val postedAt: Long? = null
)

fun PostDto.toPost(): Post = Post(
    id = id?.toLongOrNull() ?: 0L,
    type = runCatching { PostType.valueOf(type.orEmpty()) }.getOrDefault(PostType.LOST),
    status = runCatching { PostStatus.valueOf(status.orEmpty()) }.getOrDefault(PostStatus.SEARCHING),
    title = title.orEmpty(),
    category = runCatching { ItemCategory.valueOf(category.orEmpty()) }.getOrDefault(ItemCategory.OTHER),
    description = description.orEmpty(),
    imageUrl = imageUrl,
    location = location.orEmpty(),
    contact = contact.orEmpty(),
    authorName = authorName.orEmpty(),
    hiddenDetail = hiddenDetail,
    ownerEmail = ownerEmail.orEmpty(),
    createdAt = postedAt ?: System.currentTimeMillis()
)

// Dùng khi gửi tin lên server. Không gửi id: để server tự cấp.
fun Post.toDto(): PostDto = PostDto(
    type = type.name,
    status = status.name,
    title = title,
    category = category.name,
    description = description,
    imageUrl = imageUrl,
    location = location,
    contact = contact,
    authorName = authorName,
    hiddenDetail = hiddenDetail,
    ownerEmail = ownerEmail,
    postedAt = createdAt
)