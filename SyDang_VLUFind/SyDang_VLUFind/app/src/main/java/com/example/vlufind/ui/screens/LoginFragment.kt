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

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Khai báo SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo SharedPreferences (Tạo file tên là "LoginPrefs")
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // 1. Tự động điền dữ liệu nếu trước đó đã tick "Nhớ mật khẩu"
        val isRemembered = sharedPreferences.getBoolean("REMEMBER", false)
        if (isRemembered) {
            binding.etEmail.setText(sharedPreferences.getString("EMAIL", ""))
            binding.etPassword.setText(sharedPreferences.getString("PASSWORD", ""))
            binding.cbRememberMe.isChecked = true
        }

        // Đăng nhập: kiểm tra email và mật khẩu
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            // Kiểm tra email không được để trống
            if (email.isEmpty()) {
                binding.etEmail.error = "Vui lòng nhập email bạn đã đăng ký"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            // Kiểm tra định dạng email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email không đúng định dạng"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            // Kiểm tra email có đúng đuôi @vanlanguni.vn
            if (!email.endsWith("@vanlanguni.vn", ignoreCase = true) &&
                !email.endsWith("@gmail.com", ignoreCase = true)) {
                binding.etEmail.error = "Email phải có đuôi @vanlanguni.vn hoặc @gmail.com"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            // Kiểm tra mật khẩu không được để trống
            if (password.isBlank()) {
                binding.etPassword.error = "Vui lòng nhập mật khẩu"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            // Kiểm tra độ dài mật khẩu
            if (password.length < 6) {
                binding.etPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            // 2. Lưu hoặc Xóa thông tin đăng nhập tùy vào trạng thái của CheckBox
            val editor = sharedPreferences.edit()
            if (binding.cbRememberMe.isChecked) {
                // Nếu có tick -> Lưu email, password và trạng thái true
                editor.putString("EMAIL", email)
                editor.putString("PASSWORD", password)
                editor.putBoolean("REMEMBER", true)
            } else {
                // Nếu không tick -> Xóa dữ liệu cũ
                editor.clear()
            }
            editor.putString("CURRENT_EMAIL", email)
            editor.apply() // Xác nhận lưu

            // Vượt qua kiểm tra đầu vào: chuyển sang Home
            Toast.makeText(
                requireContext(),
                "Kiểm tra thông tin thành công!",
                Toast.LENGTH_SHORT
            ).show()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // Chuyển sang màn hình Đăng ký
        binding.btnGoToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        // Xử lý nút Quên mật khẩu
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Chức năng quên mật khẩu sẽ được bổ sung sau.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (bottomNav != null) {
            bottomNav.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}