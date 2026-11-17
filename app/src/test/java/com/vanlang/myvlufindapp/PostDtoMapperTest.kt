package com.vanlang.myvlufindapp

import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType
import com.vanlang.myvlufindapp.data.remote.PostDto
import com.vanlang.myvlufindapp.data.remote.toDto
import com.vanlang.myvlufindapp.data.remote.toPost
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PostDtoMapperTest {

    @Test
    fun validDto_isMappedCorrectly() {
        val dto = PostDto(
            id = "7",
            type = "FOUND",
            status = "WAITING",
            title = "Nhặt được thẻ sinh viên",
            category = "STUDENT_CARD",
            description = "Nhặt ở hành lang",
            location = "Thư viện",
            contact = "0900 000 002",
            authorName = "Sinh viên B",
            hiddenDetail = "Mã số sinh viên",
            ownerEmail = "b@example.com",
            postedAt = 123L
        )

        val post = dto.toPost()

        assertEquals(7L, post.id)
        assertEquals(PostType.FOUND, post.type)
        assertEquals(PostStatus.WAITING, post.status)
        assertEquals(ItemCategory.STUDENT_CARD, post.category)
        assertEquals("Thư viện", post.location)
        assertEquals("b@example.com", post.ownerEmail)
        assertEquals(123L, post.createdAt)
    }

    @Test
    fun invalidEnumValues_fallBackToDefaults() {
        val dto = PostDto(type = "???", status = "abc", category = "xyz")

        val post = dto.toPost()

        assertEquals(PostType.LOST, post.type)
        assertEquals(PostStatus.SEARCHING, post.status)
        assertEquals(ItemCategory.OTHER, post.category)
    }

    @Test
    fun nullFields_doNotCrash() {
        val post = PostDto().toPost()

        assertEquals(0L, post.id)
        assertEquals("", post.title)
        assertEquals("", post.location)
        assertNull(post.imageUrl)
    }

    @Test
    fun toDto_doesNotSendId_andKeepsEnumNames() {
        val post = Post(
            id = 5,
            type = PostType.LOST,
            status = PostStatus.SEARCHING,
            title = "Mất ví",
            category = ItemCategory.WALLET,
            description = "Ví da",
            location = "Căng tin",
            contact = "0900",
            authorName = "A"
        )

        val dto = post.toDto()

        assertNull(dto.id)
        assertEquals("LOST", dto.type)
        assertEquals("SEARCHING", dto.status)
        assertEquals("WALLET", dto.category)
    }
}