package com.example.vlufind.data

import android.content.Context
import com.example.vlufind.model.ItemCategory
import com.example.vlufind.model.LostFoundPost
import com.example.vlufind.model.PostStatus
import com.example.vlufind.model.PostType
import com.example.vlufind.model.User
import org.json.JSONArray
import org.json.JSONObject

object MockData {

    private const val PREF_NAME = "VLUFindPosts"
    private const val POST_KEY = "saved_posts"
    private const val LAST_ID_KEY = "last_post_id"

    private var lastPostId = 10

    val users = listOf(
        User(1, "Nguyễn Sỹ Đăng", "2474802010085", "0375064672"),
        User(2, "Trần Thị Bích", "2474802010086", "0901958896"),
        User(3, "Lê Minh Cường", "2474802010087", "0901044403"),
        User(4, "Phạm Thu Dung", "2474802010088", "0901666004"),
        User(5, "Hoàng Gia Huy", "2474802010089", "0301058205")
    )

    private const val LAT = 10.8285
    private const val LNG = 106.6990

    val posts = mutableListOf(

        LostFoundPost(
            1, PostType.LOST, "Mất ví da màu nâu",
            "Trong ví có thẻ sinh viên và vài tờ tiền mặt. Mất khoảng giờ ra chơi.",
            ItemCategory.WALLET, "Căn tin",
            LAT + 0.0001, LNG + 0.0001,
            users[0], "2026-10-03 09:15"
        ),

        LostFoundPost(
            2, PostType.FOUND, "Nhặt được thẻ sinh viên",
            "Thẻ của sinh viên khoa CNTT, đang gửi ở bảo vệ sảnh chính.",
            ItemCategory.STUDENT_CARD, "Sảnh tòa A",
            LAT + 0.0002, LNG,
            users[1], "2026-10-04 14:30"
        ),

        LostFoundPost(
            3, PostType.LOST, "Mất iPhone ốp lưng trong suốt",
            "Có dán hình mèo ở mặt sau ốp. Có thể để quên ở bàn học.",
            ItemCategory.PHONE, "Thư viện tầng 6",
            LAT, LNG + 0.0002,
            users[2], "2026-10-04 16:45"
      ),

        LostFoundPost(
            4, PostType.FOUND, "Nhặt được chùm chìa khóa xe máy",
            "Có móc khóa hình gấu nhỏ màu vàng.",
            ItemCategory.KEYS, "Bãi giữ xe",
            LAT - 0.0001, LNG + 0.0001,
            users[3], "2026-10-02 11:00"
        ),

        LostFoundPost(
            5, PostType.LOST, "Mất balo màu đen",
            "Trong balo có laptop và sách giáo trình. Rất mong được trả lại.",
            ItemCategory.BAG, "Phòng học A.301",
            LAT + 0.0003, LNG - 0.0001,
            users[4], "2026-10-01 17:20"
        ),

        LostFoundPost(
            6, PostType.FOUND, "Nhặt được tai nghe không dây",
            "Hộp tai nghe màu trắng, để quên ở ghế đá.",
            ItemCategory.ELECTRONICS, "Sân trường gần tòa K",
            LAT, LNG - 0.0002,
            users[0], "2026-10-05 08:10"
        ),

        LostFoundPost(
            7, PostType.LOST, "Mất sách Cấu trúc dữ liệu",
            "Bìa sách có tên viết tay ở trang đầu.",
            ItemCategory.BOOK, "Thư viện tầng 6",
            LAT + 0.0001, LNG + 0.0003,
            users[1], "2026-09-30 10:05"
        ),

        LostFoundPost(
            8, PostType.FOUND, "Nhặt được ví nữ màu hồng",
            "Có thẻ ATM và thẻ sinh viên. Đã gửi phòng công tác sinh viên.",
            ItemCategory.WALLET, "Cổng trường",
            LAT - 0.0002, LNG,
            users[2], "2026-10-03 18:00",
            PostStatus.RETURNED
        ),

        LostFoundPost(
            9, PostType.LOST, "Mất máy tính cầm tay Casio",
            "Mặt sau có dán tên bằng băng keo trắng.",
            ItemCategory.ELECTRONICS, "Phòng thi F.12.04",
            LAT + 0.0002, LNG + 0.0002,
            users[3], "2026-10-02 15:30"
        ),

        LostFoundPost(
            10, PostType.FOUND, "Nhặt được áo khoác xanh navy",
            "Treo ở lan can sau buổi học chiều.",
            ItemCategory.OTHER, "Hành lang tòa F tầng 3",
            LAT, LNG,
            users[4], "2026-10-04 19:00"
        )
    )

    fun addPost(post: LostFoundPost) {
        posts.add(0, post)
        lastPostId = maxOf(lastPostId, post.id)
    }

    fun addPost(
        context: Context,
        post: LostFoundPost
    ): Boolean {

        if (posts.any { it.id == post.id }) {
            return false
        }

        val oldLastId = lastPostId

        posts.add(0, post)

        lastPostId = maxOf(lastPostId, post.id)

        if (!savePosts(context)) {

            posts.removeAt(0)
            lastPostId = oldLastId

            return false
        }

        return true
    }

    fun deletePost(
        context: Context,
        postId: Int,
        userId: Int
    ): Boolean {

        val index = posts.indexOfFirst {
            it.id == postId &&
                    it.postedBy.id == userId
        }

        if (index == -1) {
            return false
        }

        val deletedPost = posts.removeAt(index)

        lastPostId = maxOf(
            lastPostId,
            deletedPost.id
        )

        if (!savePosts(context)) {

            posts.add(index, deletedPost)

            return false
        }

        val favoritePrefs = context.getSharedPreferences(
            "vlufind_favorites",
            Context.MODE_PRIVATE
        )

        val favoriteIds = favoritePrefs
            .getStringSet("favorite_ids", emptySet())
            .orEmpty()
            .toMutableSet()

        favoriteIds.remove(postId.toString())

        favoritePrefs.edit()
            .putStringSet("favorite_ids", favoriteIds)
            .apply()

        return true
    }

    fun getNextPostId(): Int {

        return maxOf(
            lastPostId,
            posts.maxOfOrNull { it.id } ?: 0
        ) + 1
    }

    fun lostPosts() = posts.filter {
        it.type == PostType.LOST
    }

    fun foundPosts() = posts.filter {
        it.type == PostType.FOUND
    }

    fun search(keyword: String) = posts.filter {

        it.title.contains(
            keyword,
            ignoreCase = true
        ) ||

                it.description.contains(
                    keyword,
                    ignoreCase = true
                )
    }

    fun savePosts(context: Context): Boolean {

        return try {

            val jsonArray = JSONArray()

            posts.forEach { post ->

                val json = JSONObject()

                json.put("id", post.id)
                json.put("type", post.type.name)
                json.put("title", post.title)
                json.put("description", post.description)
                json.put("category", post.category.name)
                json.put("location", post.location)
                json.put("lat", post.lat)
                json.put("lng", post.lng)
                json.put("postedAt", post.postedAt)
                json.put("status", post.status.name)
                json.put("imageUri", post.imageUri ?: "")

                val user = JSONObject()

                user.put("id", post.postedBy.id)
                user.put("fullName", post.postedBy.fullName)
                user.put("studentId", post.postedBy.studentId)
                user.put("phone", post.postedBy.phone)

                json.put("postedBy", user)

                jsonArray.put(json)
            }

            context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            ).edit()
                .putString(
                    POST_KEY,
                    jsonArray.toString()
                )
                .putInt(
                    LAST_ID_KEY,
                    lastPostId
                )
                .commit()

        } catch (e: Exception) {

            e.printStackTrace()

            false
        }
    }

    fun loadPosts(context: Context) {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        lastPostId = preferences.getInt(
            LAST_ID_KEY,
            10
        )

        val savedData = preferences.getString(
            POST_KEY,
            null
        ) ?: return

        try {

            val jsonArray = JSONArray(savedData)

            val loadedPosts = mutableListOf<LostFoundPost>()

            for (i in 0 until jsonArray.length()) {

                val json = jsonArray.getJSONObject(i)

                val userJson = json.getJSONObject(
                    "postedBy"
                )

                val user = User(

                    id = userJson.getInt("id"),

                    fullName = userJson.getString(
                        "fullName"
                    ),

                    studentId = userJson.getString(
                        "studentId"
                    ),

                    phone = userJson.getString("phone")
                )

                val post = LostFoundPost(

                    id = json.getInt("id"),

                    type = PostType.valueOf(
                        json.getString("type")
                    ),

                    title = json.getString("title"),

                    description = json.getString(
                        "description"
                    ),

                    category = ItemCategory.valueOf(
                        json.getString("category")
                    ),

                    location = json.getString("location"),

                    lat = json.getDouble("lat"),

                    lng = json.getDouble("lng"),

                    postedBy = user,

                    postedAt = json.getString("postedAt"),

                    status = PostStatus.valueOf(
                        json.getString("status")
                    ),

                    imageUri = json.optString("imageUri")
                        .takeIf { it.isNotBlank() }
                )

                loadedPosts.add(post)
            }

            posts.clear()

            posts.addAll(loadedPosts)

            lastPostId = maxOf(
                lastPostId,
                posts.maxOfOrNull { it.id } ?: 10
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}