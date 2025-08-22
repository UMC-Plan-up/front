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
import com.example.planup.goal.data.GoalDto
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch
import android.util.Log
import androidx.core.graphics.toColorInt
import com.example.planup.databinding.FragmentCommonGoalBinding
import com.example.planup.databinding.ItemGoalCardBinding

class CommonGoalFragment : Fragment() {

    private var _binding: FragmentCommonGoalBinding? = null
    private val binding get() = _binding!!

    private var isFriendTab = true
    private var showAll = false

    private var goalOwnerName: String = "사용자"
    private var goalCategory: String = "STUDYING"

    private var currentFilteredGoals: List<GoalDto> = emptyList()

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
                .navigateToFragment(GoalSelectFragment())
//                .navigateToFragment(GoalCategoryFragment())
        }

        /* 기본 탭: 친구와 함께 활성화 */
        setTabActive(binding.friendTab, true)
        setTabActive(binding.communityTab, false)

        fetchGoalsFromServer("FRIEND")

        /* 탭 전환 (친구 ↔ 커뮤니티) */
        binding.friendTab.setOnClickListener {
            isFriendTab = true
            showAll = false
            setTabActive(binding.friendTab, true)
            setTabActive(binding.communityTab, false)
            fetchGoalsFromServer("FRIEND")
        }

        binding.communityTab.setOnClickListener {
            isFriendTab = false
            showAll = false
            setTabActive(binding.friendTab, false)
            setTabActive(binding.communityTab, true)
            fetchGoalsFromServer("COMMUNITY")
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
                val res = if (goalType.equals("FRIEND", ignoreCase = true)) {
                    RetrofitInstance.goalApi.getFriendGoalsByCategory(goalCategory)
                } else {
                    RetrofitInstance.goalApi.getCommunityGoalsByCategory(goalCategory)
                }

                if (res.isSuccessful) {
                    val list = res.body()?.result.orEmpty()
                    currentFilteredGoals = list
                    renderFirstThree(list)
                } else {
                    Log.e("GoalAPI", "code=${res.code()} msg=${res.errorBody()?.string()}")
                    renderFirstThree(emptyList())
                }
            } catch (e: Exception) {
                Log.e("GoalAPI", "network error", e)
                renderFirstThree(emptyList())
            }
        }
    }

    /* 목표 카드를 goalContainer에 추가 */
    private fun renderFirstThree(goals: List<GoalDto>) {
        binding.goalCardContainer.removeAllViews()

        val first = goals.take(3)
        val rest = goals.drop(3)

        addCards(first)

        binding.moreButton.visibility = if (rest.isNotEmpty()) View.VISIBLE else View.GONE
        binding.moreButton.setOnClickListener {
            if (!showAll) {
                showAll = true
                addCards(rest)
                binding.moreButton.visibility = View.GONE
            }
        }
    }

    private fun addCards(list: List<GoalDto>) {
        val inflater = LayoutInflater.from(requireContext())
        list.forEach { goal ->
            val card = ItemGoalCardBinding.inflate(inflater, binding.goalCardContainer, false)

            Glide.with(this)
                .load(goal.creatorProfileImg.takeIf { it.isNotBlank() })
                .placeholder(R.drawable.ic_profile_green)
                .into(card.profileImage)

            card.goalOwner.text = goal.creatorNickname
            card.memberCount.text = getString(R.string.goal_member_count, goal.participantCount)

            val isTimer = goal.verificationType.equals("TIMER", ignoreCase = true) && goal.goalTime > 0
            if (isTimer) {
                card.certCamera.visibility = View.GONE
                card.goalTime.visibility = View.VISIBLE
                card.goalTime.text = formatSecondsToHhMmSs(goal.goalTime)
            } else {
                card.goalTime.visibility = View.GONE
                card.certCamera.visibility = View.VISIBLE
            }

            card.goalTitle.text = goal.goalName

            val period = periodKoFromGoalType(goal.goalType)
            card.goalFrequency.text = "$period ${goal.frequency}번 이상"
            card.goalDescription.text = getString(R.string.goal_one_dose, goal.oneDose)

            binding.goalCardContainer.addView(card.root)
        }
    }

    private fun periodKoFromGoalType(type: String?): String = when (type?.uppercase()) {
        "DAILY", "DAY"     -> "매일"
        "WEEKLY", "WEEK"   -> "매주"
        "MONTHLY", "MONTH" -> "매월"
        else               -> "매주"
    }

    private fun formatSecondsToHhMmSs(totalSeconds: Int): String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
