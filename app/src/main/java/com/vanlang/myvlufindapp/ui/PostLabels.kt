package com.vanlang.myvlufindapp.ui

import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType

fun PostType.label(): String = when (this) {
    PostType.LOST -> "Mất đồ"
    PostType.FOUND -> "Nhặt được"
}

fun PostStatus.label(): String = when (this) {
    PostStatus.SEARCHING -> "Đang tìm"
    PostStatus.FOUND_BACK -> "Đã tìm lại"
    PostStatus.WAITING -> "Chờ nhận"
    PostStatus.RETURNED -> "Đã trao trả"
}

fun ItemCategory.label(): String = when (this) {
    ItemCategory.WALLET -> "Ví tiền"
    ItemCategory.STUDENT_CARD -> "Thẻ sinh viên"
    ItemCategory.KEYS -> "Chìa khóa"
    ItemCategory.PHONE -> "Điện thoại"
    ItemCategory.LAPTOP -> "Laptop"
    ItemCategory.OTHER -> "Khác"
}