package com.example.planup.main.home.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendGoalListBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.goal.item.FriendGoalListResponse
import com.example.planup.main.goal.item.FriendGoalListResult
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import com.example.planup.main.home.adapter.FriendGoalListAdapter
import com.example.planup.main.home.item.FriendGoalListItem
import kotlinx.coroutines.launch

class FriendGoalListFragment : Fragment() {

    // binding은 nullable로 관리
    private var _binding: FragmentFriendGoalListBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalAdapter: FriendGoalListAdapter
    private var friendId: Int = 0
    private lateinit var friendName: String
    private lateinit var friendProfileImage: String
    private val sampleItems = listOf(
        FriendGoalListItem(1,"헬스장 가기", "매주 3번 이상", "헬스장 가서 30분 채우고 오기", 85),
        FriendGoalListItem(2, "물 마시기", "하루 2L", "수분 보충 챙기기", 40)
    )
    private var goalList = listOf<FriendGoalListResult>(
    )
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendGoalListBinding.inflate(inflater, container, false)
        friendId = arguments?.getInt("friendId") ?: 0
        friendName = arguments?.getString("friendName") ?: ""
        friendProfileImage = arguments?.getString("friendResId") ?: ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)

        Glide.with(this).load(friendProfileImage)
            .circleCrop().into(binding.friendGoalListProfileIv)

        binding.friendGoalListTv.text = "${friendName}의 목표 리스트"
        loadFriendGoalList(token = "Bearer $token", friendId = friendId.toInt())

        goalAdapter = FriendGoalListAdapter(sampleItems) { item ->
            val detailFragment = FriendGoalDetailFragment()
            val bundle = Bundle().apply {
                putInt("friendId", friendId)
                putInt("goalId", item.goalId)
                putString("title", item.title)
            }
            detailFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.friendGoalListRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalAdapter
        }

        binding.friendGoalListBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }

    private fun loadFriendGoalList(token: String, friendId: Int) {
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getFriendGoalList(token = "Bearer $token", friendId = friendId)
                if (response.isSuccess) {
                    for(item in response.result) {
                        goalList+=item
                    }
                } else {
                    Log.d("FriendGoalListFragmentApi", "loadFriendGoalList 호출 실패: ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("FriendGoalListFragmentApi", "loadFriendGoalList 오류: ${e.message}")
            }
        }
    }
}
