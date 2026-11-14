package com.vanlang.myvlufindapp.data.repository

import androidx.lifecycle.LiveData
import com.vanlang.myvlufindapp.data.local.PostDao
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType
import com.vanlang.myvlufindapp.data.remote.PostApi
import com.vanlang.myvlufindapp.data.remote.toDto
import com.vanlang.myvlufindapp.data.remote.toPost

class PostRepository(
    private val postDao: PostDao,
    private val postApi: PostApi
) {

    val allPosts: LiveData<List<Post>> = postDao.getAllPosts()

    fun getPostsByType(type: PostType): LiveData<List<Post>> = postDao.getPostsByType(type)

    fun getPostById(id: Long): LiveData<Post?> = postDao.getPostById(id)

    fun searchPosts(keyword: String): LiveData<List<Post>> = postDao.searchPosts(keyword)

    fun getMyPosts(email: String): LiveData<List<Post>> = postDao.getPostsByOwner(email)

    suspend fun insert(post: Post): Long = postDao.insert(post)

    suspend fun update(post: Post) = postDao.update(post)

    suspend fun delete(post: Post) = postDao.delete(post)

    // Lấy dữ liệu từ Web API, rồi lưu vào Room làm bộ nhớ đệm.
    // Nếu gọi API lỗi thì ném lỗi ra ngoài và KHÔNG xóa dữ liệu cũ trong máy.
    suspend fun refreshPosts() {
        val remotePosts = postApi.getPosts()
        postDao.deleteAll()
        postDao.insertAll(remotePosts.map { it.toPost() })
    }

    // Gửi tin mới lên Web API; thành công thì lưu luôn vào Room (kèm id server cấp)
    suspend fun createPost(post: Post) {
        val created = postApi.createPost(post.toDto())
        postDao.insert(created.toPost())
    }

    // Đổi trạng thái: gửi lên server trước, thành công thì cập nhật Room
    suspend fun changeStatus(post: Post, newStatus: PostStatus) {
        val updated = post.copy(status = newStatus)
        postApi.updatePost(post.id, updated.toDto())
        postDao.update(updated)
    }
}