package com.vanlang.myvlufindapp

import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType
import com.vanlang.myvlufindapp.ui.search.SearchFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFilterTest {

    private fun post(
        title: String,
        description: String = "",
        location: String = "",
        type: PostType = PostType.LOST,
        category: ItemCategory = ItemCategory.OTHER
    ) = Post(
        type = type,
        status = PostStatus.SEARCHING,
        title = title,
        category = category,
        description = description,
        location = location,
        contact = "",
        authorName = ""
    )

    @Test
    fun emptyKeyword_matchesEverything() {
        assertTrue(SearchFilter.matches(post("Mất ví da"), "", null, null))
    }

    @Test
    fun keywordWithoutDiacritics_matchesVietnameseTitle() {
        assertTrue(SearchFilter.matches(post("Mất ví da màu nâu"), "vi", null, null))
        assertTrue(SearchFilter.matches(post("Mất điện thoại màu đen"), "dien thoai", null, null))
    }

    @Test
    fun keyword_matchesLocationAndDescription() {
        assertTrue(SearchFilter.matches(post("Mất đồ", location = "Thư viện"), "thu vien", null, null))
        assertTrue(SearchFilter.matches(post("Mất đồ", description = "Ốp lưng trong suốt"), "op lung", null, null))
    }

    @Test
    fun keywordNotFound_doesNotMatch() {
        assertFalse(SearchFilter.matches(post("Mất ví"), "laptop", null, null))
    }

    @Test
    fun typeFilter_excludesOtherType() {
        val found = post("Nhặt được chìa khóa", type = PostType.FOUND)
        assertTrue(SearchFilter.matches(found, "", PostType.FOUND, null))
        assertFalse(SearchFilter.matches(found, "", PostType.LOST, null))
    }

    @Test
    fun categoryFilter_excludesOtherCategory() {
        val phone = post("Mất điện thoại", category = ItemCategory.PHONE)
        assertTrue(SearchFilter.matches(phone, "", null, ItemCategory.PHONE))
        assertFalse(SearchFilter.matches(phone, "", null, ItemCategory.WALLET))
    }

    @Test
    fun normalize_removesDiacriticsAndCase() {
        assertEquals("duong dan", SearchFilter.normalize("Đường Dẫn"))
    }
}