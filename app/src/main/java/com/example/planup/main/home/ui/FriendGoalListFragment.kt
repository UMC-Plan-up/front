package com.example.planup.main.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendGoalListBinding
import com.example.planup.main.home.adapter.FriendGoalListAdapter
import com.example.planup.main.home.item.FriendGoalListItem

class FriendGoalListFragment : Fragment() {

    // binding은 nullable로 관리
    private var _binding: FragmentFriendGoalListBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalAdapter: FriendGoalListAdapter
    private lateinit var friendId: String
    private lateinit var friendName: String
    private lateinit var friendProfileImage: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendGoalListBinding.inflate(inflater, container, false)
        friendId = arguments?.getString("friendId") ?: ""
        friendName = arguments?.getString("friendName") ?: ""
        friendProfileImage = arguments?.getString("friendResId") ?: ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sampleItems = listOf(
            FriendGoalListItem("헬스장 가기", "매주 3번 이상", "헬스장 가서 30분 채우고 오기", 85),
            FriendGoalListItem("물 마시기", "하루 2L", "수분 보충 챙기기", 40)
        )

        Glide.with(this).load(friendProfileImage)
            .circleCrop().into(binding.friendGoalListProfileIv)

        binding.friendGoalListTv.text = "${friendName}의 목표 리스트"

        goalAdapter = FriendGoalListAdapter(sampleItems) { item ->
            val detailFragment = FriendGoalDetailFragment()
            val bundle = Bundle().apply {
                putString("friendId", friendId)
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
}
