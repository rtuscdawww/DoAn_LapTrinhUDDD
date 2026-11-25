package com.example.vlufind.ui.screens

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.vlufind.R
import com.example.vlufind.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.util.UUID

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()

    private val redColor = Color.parseColor("#B71C1C")
    private val grayColor = Color.parseColor("#757575")

    private val pickAvatar = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            saveAvatar(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        binding.etProfileName.setText("Sinh viên Văn Lang")
        binding.tvProfileEmail.text = getCurrentEmail()

        loadAvatar()

        binding.imgEditAvatar.setOnClickListener {
            pickAvatar.launch("image/*")
        }

        binding.imgAvatar.setOnClickListener {
            pickAvatar.launch("image/*")
        }

        binding.btnMyPosts.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    MyPostsFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        binding.btnSavedPosts.setOnClickListener {

            val bottomNav = requireActivity()
                .findViewById<BottomNavigationView>(
                    R.id.bottom_navigation
                )

            bottomNav.selectedItemId = R.id.nav_favorite
        }

        binding.btnSettings.setOnClickListener {
            showSettings()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun getAvatarKey(): String {
        return "avatar_${getCurrentEmail().lowercase()}"
    }

    private fun saveAvatar(uri: Uri) {

        val context = requireContext()

        val folder = File(
            context.filesDir,
            "avatars"
        )

        if (!folder.exists()) {
            folder.mkdirs()
        }

        val file = File(
            folder,
            UUID.randomUUID().toString()
        )

        try {

            val input = context.contentResolver
                .openInputStream(uri)
                ?: throw IllegalStateException("Không đọc được ảnh")

            input.use { inputStream ->

                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            val preferences = context.getSharedPreferences(
                "ProfilePrefs",
                Context.MODE_PRIVATE
            )

            val oldPath = preferences.getString(
                getAvatarKey(),
                null
            )

            val saved = preferences.edit()
                .putString(
                    getAvatarKey(),
                    file.absolutePath
                )
                .commit()

            if (!saved) {
                throw IllegalStateException("Không lưu được ảnh")
            }

            oldPath?.let {
                File(it).delete()
            }

            loadAvatar()

            Toast.makeText(
                context,
                "Đã cập nhật ảnh đại diện!",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {

            file.delete()

            Toast.makeText(
                context,
                "Không thể lưu ảnh đại diện",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadAvatar() {

        val preferences = requireContext()
            .getSharedPreferences(
                "ProfilePrefs",
                Context.MODE_PRIVATE
            )

        val path = preferences.getString(
            getAvatarKey(),
            null
        )

        if (path != null && File(path).exists()) {

            binding.imgAvatar.apply {

                clearColorFilter()
                imageTintList = null

                setPadding(0, 0, 0, 0)

                setImageURI(null)
                setImageURI(Uri.fromFile(File(path)))

                scaleType = ImageView.ScaleType.CENTER_CROP

                clipToOutline = true
            }
        }
    }

    private fun getCurrentEmail(): String {

        val sharedPreferences = requireActivity()
            .getSharedPreferences(
                "LoginPrefs",
                Context.MODE_PRIVATE
            )

        return sharedPreferences
            .getString("CURRENT_EMAIL", "")
            .orEmpty()
            .ifBlank { "Chưa cập nhật email" }
    }

    private fun showSettings() {

        val options = arrayOf(
            "Thông tin tài khoản",
            "Giới thiệu VLUFind"
        )

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Cài đặt & Trợ giúp")
            .setItems(options) { _, position ->

                when (position) {
                    0 -> showAccountInfo()
                    1 -> showAboutApp()
                }
            }
            .setNegativeButton("Đóng", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(redColor)
    }

    private fun showAccountInfo() {

        val name = binding.etProfileName.text.toString()
        val email = getCurrentEmail()

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Thông tin tài khoản")
            .setMessage(
                "Họ và tên: $name\n\n" +
                        "Email: $email\n\n" +
                        "Vai trò: Thành viên VLUFind"
            )
            .setPositiveButton("Đóng", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(redColor)
    }

    private fun showAboutApp() {

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Giới thiệu VLUFind")
            .setMessage(
                "VLUFind - Phiên bản 1.0\n\n" +
                        "Ứng dụng hỗ trợ sinh viên Văn Lang " +
                        "tìm kiếm và trao trả đồ vật thất lạc."
            )
            .setPositiveButton("Đóng", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(redColor)
    }

    private fun showLogoutDialog() {

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage(
                "Bạn có chắc chắn muốn đăng xuất khỏi VLUFind?"
            )
            .setNegativeButton("Không", null)
            .setPositiveButton("Có") { _, _ ->
                logout()
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(grayColor)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(redColor)
    }

    private fun logout() {

        auth.signOut()

        requireActivity()
            .getSharedPreferences(
                "LoginPrefs",
                Context.MODE_PRIVATE
            )
            .edit()
            .remove("CURRENT_EMAIL")
            .apply()

        val bottomNav = requireActivity()
            .findViewById<BottomNavigationView>(
                R.id.bottom_navigation
            )

        bottomNav.visibility = View.GONE

        parentFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                LoginFragment()
            )
            .commit()

        Toast.makeText(
            requireContext(),
            "Đã đăng xuất",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {

        super.onResume()

        val bottomNav = requireActivity()
            .findViewById<BottomNavigationView>(
                R.id.bottom_navigation
            )

        bottomNav.visibility = View.VISIBLE

        _binding?.tvProfileEmail?.text = getCurrentEmail()

        if (_binding != null) {
            loadAvatar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}