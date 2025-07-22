package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.databinding.FragmentFriendRequestsBinding
import com.example.planup.main.friend.data.FriendRequest
import com.example.planup.main.friend.adapter.FriendRequestAdapter

class FriendRequestsFragment : Fragment(){
    lateinit var binding: FragmentFriendRequestsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFriendRequestsBinding.inflate(inflater, container, false)

        val recyclerView = binding.friendRequestRecyclerView

        val sampleList = listOf(
            FriendRequest("친구 1", "3개의 목표 진행 중"),
            FriendRequest("친구 2", "5개의 목표 진행 중"),
            FriendRequest("친구 3", "1개의 목표 진행 중"),
            FriendRequest("친구 4", "2개의 목표 진행 중"),
            FriendRequest("친구 5", "5개의 목표 진행 중"),
            FriendRequest("친구 6", "3개의 목표 진행 중"),
        )

        recyclerView.adapter = FriendRequestAdapter(sampleList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root

    }
}