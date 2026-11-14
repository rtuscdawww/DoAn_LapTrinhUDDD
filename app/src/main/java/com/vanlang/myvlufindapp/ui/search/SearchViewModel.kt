package com.vanlang.myvlufindapp.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.vanlang.myvlufindapp.data.local.AppDatabase
import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostType
import com.vanlang.myvlufindapp.data.remote.RetrofitClient
import com.vanlang.myvlufindapp.data.repository.PostRepository
import java.text.Normalizer

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(
        AppDatabase.getInstance(application).postDao(),
        RetrofitClient.postApi
    )

    private val keyword = MutableLiveData("")
    private val typeFilter = MutableLiveData<PostType?>(null)
    private val categoryFilter = MutableLiveData<ItemCategory?>(null)

    private val _results = MediatorLiveData<List<Post>>()
    val results: LiveData<List<Post>> = _results

    init {
        _results.addSource(repository.allPosts) { applyFilters() }
        _results.addSource(keyword) { applyFilters() }
        _results.addSource(typeFilter) { applyFilters() }
        _results.addSource(categoryFilter) { applyFilters() }
    }

    fun setKeyword(text: String) {
        keyword.value = text
    }

    fun setType(type: PostType?) {
        typeFilter.value = type
    }

    fun setCategory(category: ItemCategory?) {
        categoryFilter.value = category
    }

    private fun applyFilters() {
        val all = repository.allPosts.value.orEmpty()
        val key = normalize(keyword.value.orEmpty().trim())
        val type = typeFilter.value
        val category = categoryFilter.value

        _results.value = all.filter { post ->
            (type == null || post.type == type) &&
                    (category == null || post.category == category) &&
                    (key.isEmpty() ||
                            normalize(post.title).contains(key) ||
                            normalize(post.description).contains(key) ||
                            normalize(post.location).contains(key))
        }
    }

    // Bỏ dấu và chữ hoa để tìm "vi" ra "ví", "dien thoai" ra "điện thoại"
    private fun normalize(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .replace('đ', 'd')
            .replace('Đ', 'D')
            .lowercase()
}