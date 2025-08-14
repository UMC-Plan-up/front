package com.example.planup.main.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendGoalListBinding
import com.example.planup.main.home.adapter.FriendGoalListAdapter
import com.example.planup.main.home.item.FriendGoalListItem

class FriendGoalListFragment : Fragment() {
    private lateinit var binding: FragmentFriendGoalListBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var goalAdapter: FriendGoalListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_goal_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.friendGoalListRv

        val sampleItems = listOf(
            FriendGoalListItem("헬스장 가기", "매주 3번 이상", "헬스장 가서 30분 채우고 오기", 85),
            FriendGoalListItem("물 마시기", "하루 2L", "수분 보충 챙기기", 40)
        )

        goalAdapter = FriendGoalListAdapter(sampleItems)
        recyclerView.adapter = goalAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}