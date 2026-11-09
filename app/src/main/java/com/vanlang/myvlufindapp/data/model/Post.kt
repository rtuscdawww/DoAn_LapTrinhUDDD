package com.vanlang.myvlufindapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: PostType,
    val status: PostStatus,
    val title: String,
    val category: ItemCategory,
    val description: String,
    val imageUrl: String? = null,
    val location: String,
    val contact: String,
    val authorName: String,
    val hiddenDetail: String? = null,
    val ownerEmail: String = "",
    val createdAt: Long = System.currentTimeMillis()
)