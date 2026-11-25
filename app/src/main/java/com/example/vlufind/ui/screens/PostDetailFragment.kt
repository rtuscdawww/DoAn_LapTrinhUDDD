package com.example.vlufind.ui.screens

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vlufind.databinding.FragmentItemDetailBinding
import com.example.vlufind.model.LostFoundPost
import com.example.vlufind.model.PostType

class PostDetailFragment : Fragment() {

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE).orEmpty()
        val description = arguments?.getString(ARG_DESCRIPTION).orEmpty()
        val location = arguments?.getString(ARG_LOCATION).orEmpty()
        val type = arguments?.getString(ARG_TYPE).orEmpty()

        binding.tvDetailTitle.text = title
        binding.tvDetailDescription.text = description
        binding.tvDetailLocation.text = "📍 Địa điểm: $location"

        if (type == PostType.LOST.name) {
            binding.tvStatusBadge.text = "Đang tìm"
            binding.tvStatusBadge.setBackgroundColor(
                Color.parseColor("#B71C1C")
            )
        } else {
            binding.tvStatusBadge.text = "Đã tìm thấy"
            binding.tvStatusBadge.setBackgroundColor(
                Color.parseColor("#2E7D32")
            )
        }

        binding.btnContactOwner.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Chức năng liên hệ sẽ được bổ sung sau.",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnBack.setOnClickListener {
            if (!parentFragmentManager.popBackStackImmediate()) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_LOCATION = "location"
        private const val ARG_TYPE = "type"

        fun newInstance(post: LostFoundPost): PostDetailFragment {
            return PostDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, post.title)
                    putString(ARG_DESCRIPTION, post.description)
                    putString(ARG_LOCATION, post.location)
                    putString(ARG_TYPE, post.type.name)
                }
            }
        }
    }
}