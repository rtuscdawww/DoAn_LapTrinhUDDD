package com.vanlang.myvlufindapp.ui.myposts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.vanlang.myvlufindapp.data.local.AppDatabase
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.remote.RetrofitClient
import com.vanlang.myvlufindapp.data.repository.PostRepository

class MyPostsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(
        AppDatabase.getInstance(application).postDao(),
        RetrofitClient.postApi
    )

    fun getMyPosts(email: String): LiveData<List<Post>> = repository.getMyPosts(email)
}