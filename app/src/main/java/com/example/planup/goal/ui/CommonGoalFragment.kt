package com.example.planup.goal.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity

class CommonGoalFragment : Fragment(R.layout.fragment_common_goal) {

    private lateinit var backIcon: ImageView
    private lateinit var friendTab: AppCompatButton
    private lateinit var communityTab: AppCompatButton
    private lateinit var moreButton: LinearLayout
    private lateinit var createCommunityButton: LinearLayout

    private var isFriendTab = true
    private var showAll = false

    // GoalCategoryFragment에서 전달받은 닉네임
    private var goalOwnerName: String = "사용자"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GoalCategoryFragment → CommonGoalFragment에서 넘겨준 닉네임 받기
//        goalOwnerName = requireArguments().getString("goalOwnerName") ?: "사용자"
        goalOwnerName = savedInstanceState?.getString("goalOwnerName", "사용자").toString()

        // 초기화
        backIcon = view.findViewById(R.id.backIcon)
        friendTab = view.findViewById(R.id.friendTab)
        communityTab = view.findViewById(R.id.communityTab)
        moreButton = view.findViewById(R.id.moreButton)
        createCommunityButton = view.findViewById(R.id.createCommunityButton)

        /* 뒤로가기 아이콘 → 이전 화면(GoalCategoryFragment)으로 이동 */
        backIcon.setOnClickListener {
            (requireActivity() as GoalActivity)
                .navigateToFragment(GoalCategoryFragment())
        }

        /* 기본 탭: 친구와 함께 활성화 */
        setTabActive(friendTab, true)
        setTabActive(communityTab, false)

        /* 탭 전환 (친구 ↔ 커뮤니티) */
        friendTab.setOnClickListener {
            isFriendTab = true
            setTabActive(friendTab, true)
            setTabActive(communityTab, false)
            // TODO: 친구 탭 → API에서 친구 목표 불러오기 후 리스트 갱신
        }
        communityTab.setOnClickListener {
            isFriendTab = false
            setTabActive(friendTab, false)
            setTabActive(communityTab, true)
            // TODO: 커뮤니티 탭 → API에서 커뮤니티 목표 불러오기 후 리스트 갱신
        }

        /* 더보기 버튼 */
        moreButton.setOnClickListener {
            showAll = true
            // TODO: 기존 리스트에 이어서 나머지 목표 보여주기
        }

        /* 새 목표 만들기 → GoalInputFragment 이동 */
        createCommunityButton.setOnClickListener {
            val goalInputFragment = GoalInputFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }
            (requireActivity() as GoalActivity).navigateToFragment(goalInputFragment)
        }
    }

    /* 탭 활성화/비활성화 스타일 */
    private fun setTabActive(tab: AppCompatButton, active: Boolean) {
        if (active) {
            tab.setTextColor(Color.parseColor("#5383E3"))
        } else {
            tab.setTextColor(Color.parseColor("#4B5563"))
        }
    }
}
