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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.planup.databinding.FragmentCommonGoalBinding
import com.example.planup.databinding.ItemGoalCardBinding
import com.example.planup.goal.util.goalType
import com.example.planup.main.goal.ui.GoalDescriptionFragment
import com.example.planup.main.goal.ui.GoalDescriptionFragment.Companion.ARG_GOAL_ID

class CommonGoalFragment : Fragment() {

    private var _binding: FragmentCommonGoalBinding? = null
    private val binding get() = _binding!!

    private var isFriendTab = true
    private var showAll = false

    private var goalOwnerName: String = "사용자"
    private var goalCategory: String = "STUDYING"

    private var currentFilteredGoals: List<GoalDto> = emptyList()

    private fun toServerCategory(display: String): String = when (display.trim()) {
        "공부하기" -> "STUDYING"
        "운동하기" -> "EXERCISING"
        "독서하기" -> "READING"
        "저축하기" -> "SAVING"
        "생활습관" -> "LIFESTYLE"
        "취미하기" -> "HOBBY"
        else -> display.uppercase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommonGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                systemBars.bottom + view.paddingBottom
            )
            insets
        }
        goalOwnerName = arguments?.getString("goalOwnerName") ?: "사용자"
        goalCategory = arguments?.getString("selectedCategory") ?: "STUDYING"

        Log.d("CommonGoalFragment", "닉네임: $goalOwnerName / 카테고리: $goalCategory")

        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 기본 탭: 친구와 함께
        tabToggle(friendTab = true)
        showAll = false

        fetchGoalsFromServer(goalType(true)) { goalType, goalOwnerName, goalId ->
            Log.d("CommonGoalFragment", "닉네임: $goalOwnerName / 카테고리: $goalCategory")
            listener(goalType, goalOwnerName,goalId)
        }

        // 탭 전환
        binding.friendTab.setOnClickListener {
            isFriendTab = true
            tabClick()
        }

        binding.communityTab.setOnClickListener {
            isFriendTab = false
            tabClick()
        }

        // 새 목표 만들기
        binding.createCommunityButton.setOnClickListener {
            val goalActivity = requireActivity() as GoalActivity
            goalActivity.isFriendTab = isFriendTab
            val goalInputFragment = GoalInputFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                    putString("selectedCategory",goalCategory)
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

    /* 탭에 맞춰 카드/리스트/문구 토글 */
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateCard.visibility = View.VISIBLE
            binding.goalCardContainer.visibility = View.GONE
            binding.moreButton.visibility = View.GONE

            // 문구 토글
            binding.emptyFriendText.visibility = if (isFriendTab) View.VISIBLE else View.GONE
            binding.emptyCommunityText.visibility = if (isFriendTab) View.GONE else View.VISIBLE
        } else {
            binding.emptyStateCard.visibility = View.GONE
            binding.goalCardContainer.visibility = View.VISIBLE
        }
    }

    /* 목표 리스트 받아오기 */
    private fun fetchGoalsFromServer(goalType: String, setListener: (String, String, Int) -> Unit) {
        val serverCategory = toServerCategory(goalCategory)


        binding.goalCardContainer.removeAllViews()
        binding.goalCardContainer.visibility = View.GONE
        binding.moreButton.visibility = View.GONE
        binding.emptyStateCard.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val res = if (goalType.equals("FRIEND", ignoreCase = true)) {
                    RetrofitInstance.goalApi.getFriendGoalsByCategory(serverCategory)
                } else {
                    RetrofitInstance.goalApi.getCommunityGoalsByCategory(serverCategory)
                }
                Log.d("GoalAPI", "code=${res.body()?.result} msg=${res.message()}")
                if (res.isSuccessful) {
                    val list = res.body()?.result.orEmpty()

                    list.forEachIndexed { index, goal ->
                        Log.d(
                            "GoalData",
                            "Goal #$index: goalId=${goal.goalId}, verificationType=${goal.verificationType}, goalTime=${goal.goalTime}"
                        )
                    }

                    currentFilteredGoals = list
                    renderFirstThree(list){goalOwnerName, goalId->
                        setListener(goalType,goalOwnerName,goalId)
                    }
                } else {
                    Log.e("GoalAPI", "code=${res.code()} msg=${res.errorBody()?.string()}")
                    currentFilteredGoals = emptyList()
                    renderFirstThree(emptyList()){goalOwnerName,goalId->
                        setListener(goalType,goalOwnerName,goalId)
                    }
                }
            } catch (e: Exception) {
                Log.e("GoalAPI", "network error", e)
                currentFilteredGoals = emptyList()
                renderFirstThree(emptyList()){goalOwnerName,goalId->
                    setListener(goalType,goalOwnerName,goalId)
                }
            }
        }
    }

    private fun renderFirstThree(goals: List<GoalDto>, listener: (String,Int) -> Unit) {
        binding.goalCardContainer.removeAllViews()

        if (goals.isEmpty()) {
            updateEmptyState(true)
            return
        }

        updateEmptyState(false)

        val first = goals.take(3)
        val rest = goals.drop(3)

        addCards(first){goalOwnerName,goalId->
            listener(goalOwnerName,goalId)
        }

        binding.moreButton.visibility = if (rest.isNotEmpty()) View.VISIBLE else View.GONE
        binding.moreButton.setOnClickListener {
            if (!showAll) {
                showAll = true
                addCards(rest){goalOwnerName,goalId->
                    listener(goalOwnerName,goalId)
                }
                binding.moreButton.visibility = View.GONE
            }
        }
    }

    private fun addCards(list: List<GoalDto>,listener: (String,Int) -> Unit) {
        val inflater = LayoutInflater.from(requireContext())
        list.forEach { goal ->
            val card = ItemGoalCardBinding.inflate(inflater, binding.goalCardContainer, false)
            val imageUrl = goal.creatorProfileImg?.takeIf { it.isNotBlank() }

            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(card.profileImage)

            card.goalOwner.text = goal.creatorNickname
            card.memberCount.text = getString(R.string.goal_member_count, goal.participantCount)

            val isTimer = goal.verificationType.equals("TIMER", ignoreCase = true)
            if (isTimer) {
                card.certCamera.visibility = View.GONE
                card.goalTime.visibility = View.VISIBLE

                val timeInSeconds = goal.goalTime
                card.goalTime.text = if (timeInSeconds != null && timeInSeconds > 0) {
                    formatSecondsToHhMmSs(timeInSeconds)
                } else {
                    "목표 투자 시간"
                }
            } else {
                card.goalTime.visibility = View.GONE
                card.certCamera.visibility = View.VISIBLE
            }

            card.goalTitle.text = goal.goalName

            val period = periodKoFromGoalType(goal.goalType)
            card.goalFrequency.text = "$period ${goal.frequency}번 이상"
            card.goalDescription.text = goal.oneDose.toString()
            /*
            목표 생성 되는 지 확인하고 주석 해제 필요
             */
            card.root.setOnClickListener {
                Log.d("CommonGoalFragment", "setOnClickListener")
                Log.d("CommonGoalFragment", "creatorNickname: ${goal.creatorNickname}")
                listener(goal.creatorNickname,goal.goalId)
            }
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

    private fun tabToggle(friendTab: Boolean){
        setTabActive(binding.friendTab, friendTab)
        setTabActive(binding.communityTab, !friendTab)
        binding.createCommunityText.text = if (friendTab) getString(R.string.friend_create_new)
        else getString(R.string.community_create_new)
    }

    fun listener(goalType: String, goalOwnerName: String, goalId: Int) = when(goalType){

        "FRIEND"->{
            Log.d("CommonGoalFragment", "EditGoalTitleFragment goalType: FRIEND")
            Log.d("CommonGoalFragment", "EditGoalTitleFragment goalOwnerName: $goalOwnerName")
            Log.d("CommonGoalFragment", "EditGoalTitleFragment goalId: $goalId")
            val editGoalDetailFragment = FriendEditFragment().apply {
                arguments = Bundle().apply {

                    putString("goalOwnerName", goalOwnerName)
                    putInt(ARG_GOAL_ID, goalId)
                    putBoolean("isSolo", true)
                }
            }
            (requireActivity() as GoalActivity)
                .navigateToFragment(editGoalDetailFragment)
        }
        "COMMUNITY"->{
            Log.d("CommonGoalFragment", "EditGoalTitleFragment goalType: COMMUNITY")
            Log.d("CommonGoalFragment", "EditGoalTitleFragment goalOwnerName: $goalOwnerName")
            Log.d("CommonGoalFragment", "EditGoalTitleFragment goalId: $goalId")
            val goalDetailFragment = GoalDescriptionFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                    putInt(ARG_GOAL_ID, goalId)
                    putBoolean("isSolo", true)
                }
            }
            (requireActivity() as GoalActivity)
                .navigateToFragment(goalDetailFragment)
        }
        else -> {
            Log.d("CommonGoalFragment", "EditGoalTitleFragment goalType: $goalType")
        }
    }

    private fun tabClick(){
        showAll = false
        tabToggle(friendTab = isFriendTab)
        fetchGoalsFromServer(goalType(isFriendTab)) { goalType ,goalOwnerName, goalId ->
            Log.d("CommonGoalFragment", "닉네임: $goalOwnerName / 카테고리: $goalCategory")
            listener(goalType, goalOwnerName,goalId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
