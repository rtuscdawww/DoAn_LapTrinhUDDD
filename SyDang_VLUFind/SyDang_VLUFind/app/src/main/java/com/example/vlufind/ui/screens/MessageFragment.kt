package com.example.vlufind.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vlufind.R
import com.example.vlufind.databinding.FragmentMessageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    // Dữ liệu giả lập danh sách những người đã nhắn
    private val chatUsers = listOf("Nguyễn Sỹ Đăng", "Hoàng Tuấn", "Thanh Hà", "Chủ bài đăng: Nhặt được thẻ")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvChatList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatList.adapter = ChatListAdapter(chatUsers)
    }

    override fun onResume() {
        super.onResume()
        // Ở màn hình danh sách thì HIỆN thanh Tab
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter xử lý danh sách chat
    inner class ChatListAdapter(private val users: List<String>) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvChatName)
            init {
                // Sự kiện Click: Mở khung chat chi tiết
                view.setOnClickListener {
                    val userName = users[adapterPosition]
                    val chatFragment = ChatFragment()
                    val bundle = Bundle()
                    bundle.putString("USER_NAME", userName)
                    chatFragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, chatFragment)
                        .addToBackStack(null) // Cho phép Back lại danh sách
                        .commit()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvName.text = users[position]
        }

        override fun getItemCount() = users.size
    }
}