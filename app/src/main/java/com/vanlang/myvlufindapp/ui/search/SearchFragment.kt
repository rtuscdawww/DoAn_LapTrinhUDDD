package com.vanlang.myvlufindapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vanlang.myvlufindapp.R
import com.vanlang.myvlufindapp.data.model.ItemCategory
import com.vanlang.myvlufindapp.data.model.PostType
import com.vanlang.myvlufindapp.databinding.FragmentSearchBinding
import com.vanlang.myvlufindapp.ui.home.PostAdapter
import com.vanlang.myvlufindapp.ui.label

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        val adapter = PostAdapter { post ->
            findNavController().navigate(
                R.id.postDetailFragment,
                bundleOf("postId" to post.id)
            )
        }
        binding.recyclerSearch.adapter = adapter

        // Từ khóa: lọc ngay khi gõ
        binding.editSearch.doAfterTextChanged { viewModel.setKeyword(it?.toString().orEmpty()) }

        // Loại tin: Tất cả / Mất đồ / Nhặt được
        binding.chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            val type = when (checkedIds.firstOrNull()) {
                R.id.chipLost -> PostType.LOST
                R.id.chipFound -> PostType.FOUND
                else -> null
            }
            viewModel.setType(type)
        }

        // Loại đồ: mục đầu là "Tất cả loại đồ"
        val categoryLabels = listOf(getString(R.string.category_all)) +
                ItemCategory.entries.map { it.label() }
        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryLabels
        )
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                viewModel.setCategory(if (position == 0) null else ItemCategory.entries[position - 1])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.setCategory(null)
            }
        }

        viewModel.results.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.textEmptySearch.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerSearch.adapter = null
        _binding = null
    }
}