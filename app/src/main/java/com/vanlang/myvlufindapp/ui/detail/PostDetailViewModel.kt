package com.vanlang.myvlufindapp.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanlang.myvlufindapp.data.local.AppDatabase
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.remote.RetrofitClient
import com.vanlang.myvlufindapp.data.repository.PostRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class PostDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(
        AppDatabase.getInstance(application).postDao(),
        RetrofitClient.postApi
    )

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getPost(id: Long): LiveData<Post?> = repository.getPostById(id)

    fun changeStatus(post: Post, status: PostStatus) {
        viewModelScope.launch {
            try {
                repository.changeStatus(post, status)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "Không cập nhật được trạng thái: ${e.message}"
            }
        }
    }
}