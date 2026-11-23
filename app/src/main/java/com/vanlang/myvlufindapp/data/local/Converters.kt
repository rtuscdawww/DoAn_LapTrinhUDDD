package com.vanlang.myvlufindapp.data.local

import androidx.room.TypeConverter
import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType

class Converters {

    @TypeConverter
    fun fromPostType(value: PostType): String = value.name

    @TypeConverter
    fun toPostType(value: String): PostType = PostType.valueOf(value)

    @TypeConverter
    fun fromPostStatus(value: PostStatus): String = value.name

    @TypeConverter
    fun toPostStatus(value: String): PostStatus = PostStatus.valueOf(value)

    @TypeConverter
    fun fromItemCategory(value: ItemCategory): String = value.name

    @TypeConverter
    fun toItemCategory(value: String): ItemCategory = ItemCategory.valueOf(value)
}