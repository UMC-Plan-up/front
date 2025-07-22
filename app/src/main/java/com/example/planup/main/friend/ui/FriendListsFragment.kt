package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendListsBinding
import com.example.planup.main.friend.data.FriendLists
import com.example.planup.main.friend.adapter.FriendListsAdapter

class FriendListsFragment : Fragment() {
    lateinit var binding: FragmentFriendListsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendListsBinding.inflate(inflater, container, false)

        clickListener()

        val recyclerView = binding.friendListsRecyclerView

        val sampleList = listOf(
            FriendLists("친구1"),
            FriendLists("친구2"),
            FriendLists("친구3"),
            FriendLists("친구4"),
            FriendLists("친구5"),
            FriendLists("친구6"),
            FriendLists("친구7"),
        )

        recyclerView.adapter = FriendListsAdapter(sampleList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    private fun clickListener(){
        /* 뒤로 가기 버튼 */
        binding.btnBack.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendFragment())
                .commitAllowingStateLoss()
        }

    }
}