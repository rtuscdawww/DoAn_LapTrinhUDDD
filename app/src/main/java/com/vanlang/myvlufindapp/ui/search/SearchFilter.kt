package com.vanlang.myvlufindapp.ui.search

import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostType
import java.text.Normalizer

object SearchFilter {

    // Bỏ dấu và chữ hoa để tìm "vi" ra "ví", "dien thoai" ra "điện thoại"
    fun normalize(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .replace('đ', 'd')
            .replace('Đ', 'D')
            .lowercase()

    fun matches(
        post: Post,
        keyword: String,
        type: PostType?,
        category: ItemCategory?
    ): Boolean {
        val key = normalize(keyword.trim())
        return (type == null || post.type == type) &&
                (category == null || post.category == category) &&
                (key.isEmpty() ||
                        normalize(post.title).contains(key) ||
                        normalize(post.description).contains(key) ||
                        normalize(post.location).contains(key))
    }
}