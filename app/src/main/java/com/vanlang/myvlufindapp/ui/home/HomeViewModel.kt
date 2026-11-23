package com.vanlang.myvlufindapp.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanlang.myvlufindapp.data.local.AppDatabase
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.remote.RetrofitClient
import com.vanlang.myvlufindapp.data.repository.PostRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository
    val posts: LiveData<List<Post>>

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        val dao = AppDatabase.getInstance(application).postDao()
        repository = PostRepository(dao, RetrofitClient.postApi)
        posts = repository.allPosts
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                repository.refreshPosts()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "Không tải được dữ liệu mới: ${e.message}"
            }
        }
    }
}