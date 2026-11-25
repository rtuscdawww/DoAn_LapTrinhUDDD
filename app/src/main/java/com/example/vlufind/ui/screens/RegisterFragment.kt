package com.example.vlufind.ui.screens

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vlufind.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.FirebaseApp

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseApp = FirebaseApp.initializeApp(requireContext())

        if (firebaseApp == null) {
            Toast.makeText(
                requireContext(),
                "Firebase chưa được cấu hình. Hãy kiểm tra google-services.json.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        auth = FirebaseAuth.getInstance(firebaseApp)

        // Đăng ký tài khoản
        binding.btnRegister.setOnClickListener {
            registerAccount()
        }

        // Quay về màn hình đăng nhập
        binding.tvGoToLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun registerAccount() {
        val fullName = binding.etFullName.text.toString().trim()
        val cohort = binding.etCohort.text.toString().trim()
        val major = binding.etMajor.text.toString().trim()
        val email = binding.etRegEmail.text.toString().trim()
        val password = binding.etRegPassword.text.toString()
        val confirmPassword = binding.etRegPassword1.text.toString()

        // Kiểm tra họ tên
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Vui lòng nhập họ và tên"
            binding.etFullName.requestFocus()
            return
        }

        // Kiểm tra khóa
        if (cohort.isEmpty()) {
            binding.etCohort.error = "Vui lòng nhập khóa"
            binding.etCohort.requestFocus()
            return
        }

        // Kiểm tra chuyên ngành
        if (major.isEmpty()) {
            binding.etMajor.error = "Vui lòng nhập chuyên ngành"
            binding.etMajor.requestFocus()
            return
        }

        // Kiểm tra email
        if (email.isEmpty()) {
            binding.etRegEmail.error = "Vui lòng nhập email"
            binding.etRegEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegEmail.error = "Email không đúng định dạng"
            binding.etRegEmail.requestFocus()
            return
        }

        // Kiểm tra đuôi email Văn Lang
        if (!email.endsWith("@vanlanguni.vn", ignoreCase = true) &&
            !email.endsWith("@gmail.com", ignoreCase = true)) {
            binding.etRegEmail.error =
                "Vui lòng sử dụng email có đuôi @vanlanguni.vn hoặc @gmail.com"
            binding.etRegEmail.requestFocus()
            return
        }

        // Kiểm tra mật khẩu
        if (password.length < 6) {
            binding.etRegPassword.error =
                "Mật khẩu phải có ít nhất 6 ký tự"
            binding.etRegPassword.requestFocus()
            return
        }

        // Kiểm tra xác nhận mật khẩu
        if (confirmPassword != password) {
            binding.etRegPassword1.error = "Mật khẩu xác nhận không khớp"
            binding.etRegPassword1.requestFocus()
            return
        }

        // Tránh bấm đăng ký nhiều lần
        binding.btnRegister.isEnabled = false

        // Tạo tài khoản Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (!isAdded || _binding == null) {
                    return@addOnCompleteListener
                }

                if (task.isSuccessful) {
                    val user = auth.currentUser

                    if (user == null) {
                        binding.btnRegister.isEnabled = true
                        Toast.makeText(
                            requireContext(),
                            "Không lấy được thông tin tài khoản.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnCompleteListener
                    }

                    // Lưu họ tên vào hồ sơ Firebase Authentication
                    val profileUpdates =
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener {
                            if (!isAdded || _binding == null) {
                                return@addOnCompleteListener
                            }

                            // Gửi email xác minh
                            user.sendEmailVerification()
                                .addOnCompleteListener { emailTask ->

                                    if (!isAdded || _binding == null) {
                                        return@addOnCompleteListener
                                    }

                                    binding.btnRegister.isEnabled = true

                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Đăng ký thành công! " +
                                                    "Hãy kiểm tra email để xác minh tài khoản.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Đăng xuất để yêu cầu xác minh
                                        // email trước khi đăng nhập
                                        auth.signOut()

                                        // Quay về màn hình Login
                                        parentFragmentManager.popBackStack()

                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Tạo tài khoản thành công nhưng " +
                                                    "chưa gửi được email xác minh. " +
                                                    "Vui lòng thử gửi lại sau.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }

                } else {
                    binding.btnRegister.isEnabled = true

                    val errorMessage = when (
                        task.exception?.javaClass?.simpleName
                    ) {
                        "FirebaseAuthUserCollisionException" ->
                            "Email này đã được đăng ký."

                        "FirebaseAuthWeakPasswordException" ->
                            "Mật khẩu chưa đủ mạnh."

                        "FirebaseAuthInvalidCredentialsException" ->
                            "Email không hợp lệ."

                        else ->
                            task.exception?.localizedMessage
                                ?: "Đăng ký thất bại. Vui lòng thử lại."
                    }

                    Toast.makeText(
                        requireContext(),
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}