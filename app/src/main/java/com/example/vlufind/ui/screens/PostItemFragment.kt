package com.example.vlufind.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.vlufind.R
import com.example.vlufind.data.MockData
import com.example.vlufind.databinding.FragmentPostItemBinding
import com.example.vlufind.model.ItemCategory
import com.example.vlufind.model.LostFoundPost
import com.example.vlufind.model.PostType
import com.example.vlufind.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.File
import java.util.UUID

class PostItemFragment : Fragment() {

    private var _binding: FragmentPostItemBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        if (uri != null) {

            selectedImageUri = uri

            _binding?.imgUploadPhoto?.apply {
                setImageURI(uri)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            _binding?.tvPhotoHint?.text =
                "Nhấn để thay đổi ảnh"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPostItemBinding.inflate(
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

        binding.imgUploadPhoto.setOnClickListener {
            openGallery()
        }

        binding.tvPhotoHint.setOnClickListener {
            openGallery()
        }

        binding.btnSubmitPost.setOnClickListener {
            submitPost()
        }
    }

    private fun openGallery() {

        pickImage.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {

        val folder = File(
            requireContext().filesDir,
            "post_images"
        )

        if (!folder.exists()) {
            folder.mkdirs()
        }

        val file = File(
            folder,
            UUID.randomUUID().toString()
        )

        return try {

            val input = requireContext()
                .contentResolver
                .openInputStream(uri) ?: return null

            input.use { inputStream ->

                file.outputStream().use { outputStream ->

                    inputStream.copyTo(outputStream)
                }
            }

            Uri.fromFile(file).toString()

        } catch (e: Exception) {

            file.delete()
            e.printStackTrace()
            null
        }
    }

    private fun submitPost() {

        val title = binding.etItemTitle.text
            .toString().trim()

        val description = binding.etItemDescription.text
            .toString().trim()

        val location = binding.etItemLocation.text
            .toString().trim()

        if (title.isBlank()) {

            binding.etItemTitle.error =
                "Vui lòng nhập tên đồ vật"

            binding.etItemTitle.requestFocus()
            return
        }

        if (description.isBlank()) {

            binding.etItemDescription.error =
                "Vui lòng nhập mô tả"

            binding.etItemDescription.requestFocus()
            return
        }

        if (location.isBlank()) {

            binding.etItemLocation.error =
                "Vui lòng nhập địa điểm"

            binding.etItemLocation.requestFocus()
            return
        }

        val type = if (binding.rbLost.isChecked) {
            PostType.LOST
        } else {
            PostType.FOUND
        }

        val sharedPreferences = requireActivity()
            .getSharedPreferences(
                "LoginPrefs",
                Context.MODE_PRIVATE
            )

        val email = sharedPreferences
            .getString("CURRENT_EMAIL", "")
            .orEmpty()

        val currentUser = User(
            id = email.lowercase().hashCode(),
            fullName = email.substringBefore("@")
                .ifBlank { "Sinh viên Văn Lang" },
            studentId = "",
            phone = ""
        )

        val currentDate = SimpleDateFormat(
            "yyyy-MM-dd HH:mm",
            Locale.getDefault()
        ).format(Date())

        val savedImageUri = selectedImageUri?.let {
            saveImageToInternalStorage(it)
        }

        if (selectedImageUri != null && savedImageUri == null) {

            Toast.makeText(
                requireContext(),
                "Không thể lưu ảnh. Vui lòng thử lại!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val newPost = LostFoundPost(
            id = MockData.getNextPostId(),
            type = type,
            title = title,
            description = description,
            category = getCategory(title),
            location = location,
            lat = 0.0,
            lng = 0.0,
            postedBy = currentUser,
            postedAt = currentDate,
            imageUri = savedImageUri
        )

        if (!MockData.addPost(requireContext(), newPost)) {

            savedImageUri?.let {
                Uri.parse(it).path?.let { path ->
                    File(path).delete()
                }
            }

            Toast.makeText(
                requireContext(),
                "Không thể lưu bài đăng. Vui lòng thử lại!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        Toast.makeText(
            requireContext(),
            "Đăng tin thành công!",
            Toast.LENGTH_SHORT
        ).show()

        val bottomNav = requireActivity()
            .findViewById<BottomNavigationView>(
                R.id.bottom_navigation
            )

        if (bottomNav.selectedItemId == R.id.nav_home) {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    HomeFragment()
                )
                .commit()

        } else {

            bottomNav.selectedItemId = R.id.nav_home
        }
    }

    private fun getCategory(title: String): ItemCategory {

        val text = title.lowercase()

        return when {

            text.contains("ví") ->
                ItemCategory.WALLET

            text.contains("điện thoại") ||
                    text.contains("iphone") ||
                    text.contains("samsung") ->
                ItemCategory.PHONE

            text.contains("thẻ sinh viên") ->
                ItemCategory.STUDENT_CARD

            text.contains("balo") ||
                    text.contains("túi") ->
                ItemCategory.BAG

            text.contains("chìa khóa") ->
                ItemCategory.KEYS

            text.contains("tai nghe") ||
                    text.contains("laptop") ||
                    text.contains("máy tính") ->
                ItemCategory.ELECTRONICS

            text.contains("sách") ||
                    text.contains("tài liệu") ->
                ItemCategory.BOOK

            else ->
                ItemCategory.OTHER
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}