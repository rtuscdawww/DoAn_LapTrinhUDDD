package com.vanlang.myvlufindapp.data.repository

import com.vanlang.myvlufindapp.data.remote.toDto
import androidx.lifecycle.LiveData
import com.vanlang.myvlufindapp.data.local.PostDao
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType
import com.vanlang.myvlufindapp.data.remote.PostApi
import com.vanlang.myvlufindapp.data.remote.toPost

class PostRepository(
    private val postDao: PostDao,
    private val postApi: PostApi
) {

    val allPosts: LiveData<List<Post>> = postDao.getAllPosts()

    fun getPostsByType(type: PostType): LiveData<List<Post>> = postDao.getPostsByType(type)

    fun getPostById(id: Long): LiveData<Post?> = postDao.getPostById(id)

    fun searchPosts(keyword: String): LiveData<List<Post>> = postDao.searchPosts(keyword)

    suspend fun insert(post: Post): Long = postDao.insert(post)

    suspend fun update(post: Post) = postDao.update(post)

    suspend fun delete(post: Post) = postDao.delete(post)

    suspend fun updateStatus(id: Long, status: PostStatus) = postDao.updateStatus(id, status)

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
}