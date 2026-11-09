package com.vanlang.myvlufindapp.ui.post

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

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(
        AppDatabase.getInstance(application).postDao(),
        RetrofitClient.postApi
    )

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean> = _success

    fun submit(post: Post) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.createPost(post)
                _success.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "Không đăng được tin: ${e.message}"
            }
            _loading.value = false
        }
    }

    fun onSuccessHandled() {
        _success.value = false
    }
}