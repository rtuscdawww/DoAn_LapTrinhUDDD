package com.vanlang.myvlufindapp.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.vanlang.myvlufindapp.R
import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.model.PostStatus
import com.vanlang.myvlufindapp.data.model.PostType
import com.vanlang.myvlufindapp.data.repository.AuthRepository
import com.vanlang.myvlufindapp.databinding.FragmentPostBinding
import com.vanlang.myvlufindapp.ui.label

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loggedIn = AuthRepository().currentUser != null
        binding.layoutLoginRequired.visibility = if (loggedIn) View.GONE else View.VISIBLE
        binding.scrollForm.visibility = if (loggedIn) View.VISIBLE else View.GONE
        binding.buttonGoLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        if (!loggedIn) return

        viewModel = ViewModelProvider(this)[PostViewModel::class.java]

        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            ItemCategory.entries.map { it.label() }
        )

        // Chỉ tin "nhặt được" mới có chi tiết nhận dạng ẩn
        binding.radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            binding.layoutHidden.visibility =
                if (checkedId == R.id.radioFound) View.VISIBLE else View.GONE
        }

        binding.buttonSubmit.setOnClickListener { submit() }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressPost.visibility = if (loading) View.VISIBLE else View.GONE
            binding.buttonSubmit.isEnabled = !loading
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
        viewModel.success.observe(viewLifecycleOwner) { ok ->
            if (ok) {
                viewModel.onSuccessHandled()
                Toast.makeText(requireContext(), R.string.post_success, Toast.LENGTH_SHORT).show()
                clearForm()
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }

    private fun submit() {
        val user = AuthRepository().currentUser ?: return

        val title = binding.editTitle.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()
        val location = binding.editLocation.text.toString().trim()
        val contact = binding.editContact.text.toString().trim()
        val imageUrl = binding.editImageUrl.text.toString().trim()
        val hidden = binding.editHidden.text.toString().trim()

        val titleBlank = markIfBlank(binding.layoutTitle, title)
        val descriptionBlank = markIfBlank(binding.layoutDescription, description)
        val locationBlank = markIfBlank(binding.layoutLocation, location)
        val contactBlank = markIfBlank(binding.layoutContact, contact)
        if (titleBlank || descriptionBlank || locationBlank || contactBlank) return

        binding.layoutImageUrl.error = null
        if (imageUrl.isNotEmpty() && !imageUrl.startsWith("https://")) {
            binding.layoutImageUrl.error = getString(R.string.error_image_url)
            return
        }

        val type = if (binding.radioFound.isChecked) PostType.FOUND else PostType.LOST
        val post = Post(
            type = type,
            status = if (type == PostType.FOUND) PostStatus.WAITING else PostStatus.SEARCHING,
            title = title,
            category = ItemCategory.entries[binding.spinnerCategory.selectedItemPosition],
            description = description,
            imageUrl = imageUrl.ifEmpty { null },
            location = location,
            contact = contact,
            authorName = user.email?.substringBefore("@").orEmpty(),
            hiddenDetail = if (type == PostType.FOUND && hidden.isNotEmpty()) hidden else null,
            ownerEmail = user.email.orEmpty()
        )
        viewModel.submit(post)
    }

    private fun markIfBlank(layout: TextInputLayout, text: String): Boolean {
        val blank = text.isEmpty()
        layout.error = if (blank) getString(R.string.error_required) else null
        return blank
    }

    private fun clearForm() {
        binding.editTitle.setText("")
        binding.editDescription.setText("")
        binding.editLocation.setText("")
        binding.editContact.setText("")
        binding.editImageUrl.setText("")
        binding.editHidden.setText("")
        binding.radioLost.isChecked = true
        binding.spinnerCategory.setSelection(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}