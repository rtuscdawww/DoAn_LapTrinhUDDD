package com.example.vlufind.model

enum class PostType { LOST, FOUND }

enum class PostStatus { OPEN, RETURNED }

enum class ItemCategory(val label: String) {
    WALLET("Ví"),
    PHONE("Điện thoại"),
    STUDENT_CARD("Thẻ sinh viên"),
    BAG("Balo / Túi"),
    KEYS("Chìa khóa"),
    ELECTRONICS("Đồ điện tử"),
    BOOK("Sách / Tài liệu"),
    OTHER("Khác")
}

data class User(
    val id: Int,
    val fullName: String,
    val studentId: String,
    val phone: String
)

data class LostFoundPost(
    val id: Int,
    val type: PostType,
    val title: String,
    val description: String,
    val category: ItemCategory,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val postedBy: User,
    val postedAt: String,
    val status: PostStatus = PostStatus.OPEN
)

