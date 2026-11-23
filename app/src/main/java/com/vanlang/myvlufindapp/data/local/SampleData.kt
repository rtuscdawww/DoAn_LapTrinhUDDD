package com.vanlang.myvlufindapp.data.local

import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType

object SampleData {

    val posts = listOf(
        Post(
            type = PostType.LOST,
            status = PostStatus.SEARCHING,
            title = "Mất ví da màu nâu",
            category = ItemCategory.WALLET,
            description = "Ví có thẻ sinh viên và vài giấy tờ, bị mất khoảng giờ trưa.",
            location = "Căng tin",
            contact = "0900 000 001",
            authorName = "Sinh viên A"
        ),
        Post(
            type = PostType.FOUND,
            status = PostStatus.WAITING,
            title = "Nhặt được thẻ sinh viên",
            category = ItemCategory.STUDENT_CARD,
            description = "Nhặt được thẻ sinh viên ở hành lang, đang giữ chờ chủ nhận.",
            location = "Hành lang tòa A",
            contact = "0900 000 002",
            authorName = "Sinh viên B",
            hiddenDetail = "Mã số sinh viên trên thẻ"
        ),
        Post(
            type = PostType.LOST,
            status = PostStatus.SEARCHING,
            title = "Mất điện thoại màu đen",
            category = ItemCategory.PHONE,
            description = "Điện thoại ốp lưng trong suốt, để quên ở phòng học.",
            location = "Phòng học tầng 3",
            contact = "0900 000 003",
            authorName = "Sinh viên C"
        ),
        Post(
            type = PostType.FOUND,
            status = PostStatus.WAITING,
            title = "Nhặt được chùm chìa khóa",
            category = ItemCategory.KEYS,
            description = "Chùm chìa khóa có móc hình gấu, nhặt ở thư viện.",
            location = "Thư viện",
            contact = "0900 000 004",
            authorName = "Sinh viên D"
        )
    )
}