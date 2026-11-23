package com.vanlang.myvlufindapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Post>)

    @Update
    suspend fun update(post: Post)

    @Delete
    suspend fun delete(post: Post)

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE type = :type ORDER BY createdAt DESC")
    fun getPostsByType(type: PostType): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :id")
    fun getPostById(id: Long): LiveData<Post?>

    @Query(
        "SELECT * FROM posts WHERE title LIKE '%' || :keyword || '%' " +
                "OR description LIKE '%' || :keyword || '%' ORDER BY createdAt DESC"
    )
    fun searchPosts(keyword: String): LiveData<List<Post>>

    @Query("UPDATE posts SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: PostStatus)

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun count(): Int

    @Query("DELETE FROM posts")
    suspend fun deleteAll()

    @Query("SELECT * FROM posts WHERE ownerEmail = :email ORDER BY createdAt DESC")
    fun getPostsByOwner(email: String): LiveData<List<Post>>
}