package com.vanlang.myvlufindapp.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    // onResult(null) = thành công, ngược lại là thông báo lỗi tiếng Việt
    fun login(email: String, password: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            onResult(if (task.isSuccessful) null else errorMessage(task.exception))
        }
    }

    fun register(email: String, password: String, onResult: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            onResult(if (task.isSuccessful) null else errorMessage(task.exception))
        }
    }

    fun sendPasswordReset(email: String, onResult: (String?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            onResult(if (task.isSuccessful) null else errorMessage(task.exception))
        }
    }

    fun logout() = auth.signOut()

    private fun errorMessage(e: Exception?): String = when (e) {
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "Email hoặc mật khẩu không đúng"
        is FirebaseAuthUserCollisionException -> "Email này đã được đăng ký"
        is FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu"
        is FirebaseNetworkException -> "Không có kết nối mạng"
        else -> "Có lỗi xảy ra: ${e?.message}"
    }
}