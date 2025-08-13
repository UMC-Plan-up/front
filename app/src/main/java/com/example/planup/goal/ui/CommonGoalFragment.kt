package com.example.planup.goal.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.planup.databinding.FragmentCommonGoalBinding // FragmentCommonGoalBinding 추가
import com.example.planup.databinding.ItemGoalCardBinding // ItemGoalCardBinding 추가

class CommonGoalFragment : Fragment() {

    private var _binding: FragmentCommonGoalBinding? = null
    private val binding get() = _binding!!

    private var isFriendTab = true
    private var showAll = false

    private var goalOwnerName: String = "사용자"
    private var goalCategory: String = "STUDYING"

    private var currentFilteredGoals: List<GoalItemDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommonGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as GoalActivity
        goalOwnerName = arguments?.getString("goalOwnerName") ?: "사용자"
        goalCategory = activity.goalCategory ?: "STUDYING"

        Log.d("CommonGoalFragment", "닉네임: $goalOwnerName / 카테고리: $goalCategory")

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            (requireActivity() as GoalActivity)
                .navigateToFragment(GoalCategoryFragment())
        }

        /* 기본 탭: 친구와 함께 활성화 */
        setTabActive(binding.friendTab, true)
        setTabActive(binding.communityTab, false)

        fetchGoalsFromServer("FRIEND")

        /* 탭 전환 (친구 ↔ 커뮤니티) */
        binding.friendTab.setOnClickListener {
            isFriendTab = true
            setTabActive(binding.friendTab, true)
            setTabActive(binding.communityTab, false)
            fetchGoalsFromServer("FRIEND") // [친구와 함께] 탭
        }

        binding.communityTab.setOnClickListener {
            isFriendTab = false
            setTabActive(binding.friendTab, false)
            setTabActive(binding.communityTab, true)
            fetchGoalsFromServer("COMMUNITY") // [커뮤니티와 함께] 탭
        }

        /* 더보기 버튼 */
        binding.moreButton.setOnClickListener {
            showAll = true
            displayGoalCards(currentFilteredGoals) // 전체 보여주기
        }

        /* 새 목표 만들기 → GoalInputFragment 이동 */
        binding.createCommunityButton.setOnClickListener {
            val goalInputFragment = GoalInputFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }
            (requireActivity() as GoalActivity).navigateToFragment(goalInputFragment)
        }
    }

    private fun setTabActive(tab: AppCompatButton, active: Boolean) {
        val activeBg = ColorStateList.valueOf(Color.parseColor("#CCDDFE"))
        val inactiveBg = ColorStateList.valueOf(Color.parseColor("#E4E6E8"))

        if (active) {
            tab.setTextColor("#5383E3".toColorInt())
            tab.backgroundTintList = activeBg
        } else {
            tab.setTextColor("#4B5563".toColorInt())
            tab.backgroundTintList = inactiveBg
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

                    currentFilteredGoals = all
                    displayGoalCards(all)
                    binding.moreButton.visibility = if (!showAll && all.size > 3) View.VISIBLE else View.GONE
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
        binding.goalCardContainer.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())
        val displayList = if (showAll) goals else goals.take(3)

        displayList.forEach { goal ->
            val cardBinding = ItemGoalCardBinding.inflate(inflater, binding.goalCardContainer, false)

            Glide.with(this)
                .load(goal.creatorProfileImg)
                .placeholder(R.drawable.ic_profile_green)
                .into(cardBinding.profileImage)

            cardBinding.goalOwner.text = goal.creatorNickname
            cardBinding.memberCount.text = getString(R.string.goal_member_count, goal.participantCount)
            cardBinding.goalTime.text = getString(R.string.goal_time, goal.goalTime)
            cardBinding.goalTitle.text = goal.goalName
            cardBinding.goalFrequency.text = getString(R.string.goal_frequency, goal.startTimeMinutes, goal.frequency)
            cardBinding.goalDescription.text = goal.oneDesc

            binding.goalCardContainer.addView(cardBinding.root)
        }
        binding.moreButton.visibility = if (!showAll && goals.size > 3) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
