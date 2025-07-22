package com.example.planup.friend

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendRequestsBinding

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