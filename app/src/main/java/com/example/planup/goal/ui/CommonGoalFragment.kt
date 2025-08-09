package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.data.GoalItemDto
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch
import android.util.Log
import androidx.core.graphics.toColorInt

class CommonGoalFragment : Fragment(R.layout.fragment_common_goal) {

    private lateinit var backIcon: ImageView
    private lateinit var friendTab: AppCompatButton
    private lateinit var communityTab: AppCompatButton
    private lateinit var moreButton: LinearLayout
    private lateinit var createCommunityButton: LinearLayout
    private lateinit var goalContainer: LinearLayout

    private var isFriendTab = true
    private var showAll = false

    // GoalCategoryFragment에서 전달받은 닉네임
    private var goalOwnerName: String = "사용자"

    // GoalCategoryFragment에서 전달받은 카테고리
    private var goalCategory: String = "STUDYING"

    private var currentFilteredGoals: List<GoalItemDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GoalCategoryFragment에서 넘긴 닉네임 & 카테고리 받기
        val activity = requireActivity() as GoalActivity
        goalOwnerName = arguments?.getString("goalOwnerName") ?: "사용자"
        goalCategory = activity.goalCategory ?: "STUDYING"


        Log.d("CommonGoalFragment", "닉네임: $goalOwnerName / 카테고리: $goalCategory")

        // 초기화
        backIcon = view.findViewById(R.id.backIcon)
        friendTab = view.findViewById(R.id.friendTab)
        communityTab = view.findViewById(R.id.communityTab)
        moreButton = view.findViewById(R.id.moreButton)
        createCommunityButton = view.findViewById(R.id.createCommunityButton)
        goalContainer = view.findViewById(R.id.goalCardContainer)

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            (requireActivity() as GoalActivity)
                .navigateToFragment(GoalCategoryFragment())
        }

        /* 기본 탭: 친구와 함께 활성화 */
        setTabActive(friendTab, true)
        setTabActive(communityTab, false)

        // 화면 처음 들어왔을 때 기본은 친구 탭
        fetchGoalsFromServer("FRIEND")

        /* 탭 전환 (친구 ↔ 커뮤니티) */
        friendTab.setOnClickListener {
            isFriendTab = true
            setTabActive(friendTab, true)
            setTabActive(communityTab, false)
            fetchGoalsFromServer("FRIEND") // [친구와 함께] 탭
        }

        communityTab.setOnClickListener {
            isFriendTab = false
            setTabActive(friendTab, false)
            setTabActive(communityTab, true)
            fetchGoalsFromServer("COMMUNITY") // [커뮤니티와 함께] 탭
        }

        /* 더보기 버튼 */
        moreButton.setOnClickListener {
            showAll = true
            displayGoalCards(currentFilteredGoals) // 전체 보여주기
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

    private fun setTabActive(tab: AppCompatButton, active: Boolean) {
        if (active) {
            tab.setTextColor("#5383E3".toColorInt())
        } else {
            tab.setTextColor("#4B5563".toColorInt())
        }
    }

    /* 서버에서 목표 리스트 받아오는 함수 */
    private fun fetchGoalsFromServer(goalType: String) {
        lifecycleScope.launch {
            try {
                val res = RetrofitInstance.goalApi.getGoalsByCategory(goalCategory)
                if (res.isSuccessful) {
                    val all = res.body()?.result.orEmpty()
                    Log.d("GoalAPI", "all size=${all.size}")
                    all.take(5).forEachIndexed { i, g ->
                        Log.d("GoalAPI", "[$i] type=${g.goalType}, name=${g.goalName}")
                    }

                    // 일단 필터 끄고(바로 화면 뜨는지 확인)
                    currentFilteredGoals = all
                    displayGoalCards(all)
                    moreButton.visibility = if (!showAll && all.size > 3) View.VISIBLE else View.GONE
                } else {
                    Log.e("GoalAPI", "API 오류 ${res.code()} / ${res.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GoalAPI", "네트워크 예외", e)
            }
        }
    }


    /* 목표 카드를 goalContainer에 추가 */
    private fun displayGoalCards(goals: List<GoalItemDto>) {
        goalContainer.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())
        val displayList = if (showAll) goals else goals.take(3)

        displayList.forEach { goal ->
            val cardView = inflater.inflate(R.layout.item_goal_card, goalContainer, false)

            val profileImage = cardView.findViewById<ImageView>(R.id.profileImage)
            val goalOwner = cardView.findViewById<TextView>(R.id.goalOwner)
            val memberCount = cardView.findViewById<TextView>(R.id.memberCount)
            val goalTime = cardView.findViewById<TextView>(R.id.goalTime)
            val goalTitle = cardView.findViewById<TextView>(R.id.goalTitle)
            val goalFrequency = cardView.findViewById<TextView>(R.id.goalFrequency)
            val goalDescription = cardView.findViewById<TextView>(R.id.goalDescription)

            Glide.with(this)
                .load(goal.creatorProfileImg)
                .placeholder(R.drawable.ic_profile_green)
                .into(profileImage)

            goalOwner.text = goal.creatorNickname
            memberCount.text = getString(R.string.goal_member_count, goal.participantCount)
            goalTime.text = getString(R.string.goal_time, goal.goalTime)
            goalTitle.text = goal.goalName
            goalFrequency.text = getString(R.string.goal_frequency, goal.startTimeMinutes, goal.frequency)
            goalDescription.text = goal.oneDesc

            goalContainer.addView(cardView)
        }
        moreButton.visibility = if (!showAll && goals.size > 3) View.VISIBLE else View.GONE
    }
}
