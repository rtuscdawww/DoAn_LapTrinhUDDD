
package com.example.vlufind.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vlufind.R
import com.example.vlufind.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        sharedPreferences = requireActivity()
            .getSharedPreferences(
                "LoginPrefs",
                Context.MODE_PRIVATE
            )

        // Nhớ email và mật khẩu
        val isRemembered = sharedPreferences
            .getBoolean("REMEMBER", false)

        if (isRemembered) {
            binding.etEmail.setText(
                sharedPreferences.getString("EMAIL", "")
            )

            binding.etPassword.setText(
                sharedPreferences.getString("PASSWORD", "")
            )

            binding.cbRememberMe.isChecked = true
        }

        // Đăng nhập
        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty()) {
                binding.etEmail.error =
                    "Vui lòng nhập email bạn đã đăng ký"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error =
                    "Email không đúng định dạng"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!email.endsWith("@vanlanguni.vn", ignoreCase = true) &&
                !email.endsWith("@gmail.com", ignoreCase = true)
            ) {
                binding.etEmail.error =
                    "Email phải có đuôi @vanlanguni.vn hoặc @gmail.com"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isBlank()) {
                binding.etPassword.error =
                    "Vui lòng nhập mật khẩu"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.etPassword.error =
                    "Mật khẩu phải có ít nhất 6 ký tự"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            val remember = binding.cbRememberMe.isChecked

            binding.btnLogin.isEnabled = false

            // Xác thực tài khoản bằng Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (!isAdded || _binding == null) {
                        return@addOnCompleteListener
                    }

                    binding.btnLogin.isEnabled = true

                    if (task.isSuccessful) {

                        // Lưu thông tin đăng nhập
                        val editor = sharedPreferences.edit()

                        if (remember) {
                            editor.putString("EMAIL", email)
                            editor.putString("PASSWORD", password)
                            editor.putBoolean("REMEMBER", true)
                        } else {
                            editor.remove("EMAIL")
                            editor.remove("PASSWORD")
                            editor.remove("REMEMBER")
                        }

                        editor.putString("CURRENT_EMAIL", email)
                        editor.apply()

                        Toast.makeText(
                            requireContext(),
                            "Đăng nhập thành công!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Chuyển sang trang chủ
                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                HomeFragment()
                            )
                            .commit()

                    } else {

                        auth.signOut()

                        sharedPreferences.edit()
                            .remove("CURRENT_EMAIL")
                            .apply()

                        val errorMessage = when (
                            task.exception?.javaClass?.simpleName
                        ) {
                            "FirebaseAuthInvalidUserException" ->
                                "Tài khoản không tồn tại hoặc đã bị xóa."

                            "FirebaseAuthInvalidCredentialsException" ->
                                "Email hoặc mật khẩu không chính xác."

                            "FirebaseNetworkException" ->
                                "Vui lòng kiểm tra kết nối Internet."

                            "FirebaseTooManyRequestsException" ->
                                "Bạn đã thử quá nhiều lần. Vui lòng thử lại sau."

                            else ->
                                "Đăng nhập thất bại. Vui lòng thử lại."
                        }

                        Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // Chuyển sang màn hình đăng ký
        binding.btnGoToRegister.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    RegisterFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        // Quên mật khẩu
        binding.tvForgotPassword.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty() ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            ) {
                binding.etEmail.error =
                    "Vui lòng nhập email hợp lệ"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->

                    if (!isAdded || _binding == null) {
                        return@addOnCompleteListener
                    }

                    val message = if (task.isSuccessful) {
                        "Nếu tài khoản tồn tại, hãy kiểm tra email để đặt lại mật khẩu."
                    } else {
                        "Không thể gửi yêu cầu đặt lại mật khẩu. Vui lòng thử lại."
                    }

                    Toast.makeText(
                        requireContext(),
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()

        val bottomNav = requireActivity()
            .findViewById<BottomNavigationView>(
                R.id.bottom_navigation
            )

        bottomNav?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
