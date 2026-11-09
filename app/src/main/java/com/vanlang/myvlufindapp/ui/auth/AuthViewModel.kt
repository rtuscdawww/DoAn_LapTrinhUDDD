package com.vanlang.myvlufindapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanlang.myvlufindapp.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean> = _success

    private val _resetSent = MutableLiveData(false)
    val resetSent: LiveData<Boolean> = _resetSent

    fun login(email: String, password: String) {
        _loading.value = true
        repository.login(email, password) { handle(it) }
    }

    fun register(email: String, password: String) {
        _loading.value = true
        repository.register(email, password) { handle(it) }
    }

    fun resetPassword(email: String) {
        _loading.value = true
        repository.sendPasswordReset(email) { err ->
            _loading.value = false
            if (err == null) _resetSent.value = true else _error.value = err
        }
    }

    private fun handle(err: String?) {
        _loading.value = false
        if (err == null) _success.value = true else _error.value = err
    }
}