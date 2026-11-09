package com.vanlang.myvlufindapp.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vanlang.myvlufindapp.R
import com.vanlang.myvlufindapp.data.model.Post
import com.vanlang.myvlufindapp.data.repository.AuthRepository
import com.vanlang.myvlufindapp.databinding.FragmentPostDetailBinding
import com.vanlang.myvlufindapp.ui.label
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[PostDetailViewModel::class.java]
        val postId = arguments?.getLong("postId") ?: -1L

        binding.buttonBack.setOnClickListener { findNavController().popBackStack() }

        viewModel.getPost(postId).observe(viewLifecycleOwner) { post ->
            // Khi dữ liệu đang được làm mới, post có thể tạm null: bỏ qua, giữ nguyên nội dung cũ.
            post ?: return@observe
            showPost(post)
        }
    }

    private fun showPost(post: Post) {
        binding.textDetailType.text = "${post.type.label()} • ${post.status.label()}"
        binding.textDetailTitle.text = post.title
        binding.textDetailCategory.text = getString(R.string.detail_category, post.category.label())
        binding.textDetailLocation.text = getString(R.string.detail_location, post.location)
        binding.textDetailAuthor.text = getString(R.string.detail_author, post.authorName)
        binding.textDetailDate.text = getString(R.string.detail_date, formatDate(post.createdAt))
        binding.textDetailDescription.text = post.description

        Glide.with(binding.imageDetail)
            .load(post.imageUrl)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_placeholder)
            .fallback(R.drawable.ic_image_placeholder)
            .centerCrop()
            .into(binding.imageDetail)

        binding.buttonContact.setOnClickListener {
            if (AuthRepository().currentUser == null) {
                Toast.makeText(requireContext(), R.string.login_required_message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.loginFragment)
                return@setOnClickListener
            }

            val phone = post.contact.filter { it.isDigit() || it == '+' }
            try {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), R.string.no_dialer, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatDate(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN")).format(Date(millis))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}