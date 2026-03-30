package com.planup.planup.goal.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.planup.planup.R
import com.planup.planup.databinding.FragmentGoalCommunityJoinBinding
import com.planup.planup.goal.GoalActivity
import com.planup.planup.goal.adapter.RankURLAdapter
import com.planup.planup.goal.adapter.RankURLItem
import com.planup.planup.goal.data.ReportGoal
import com.planup.planup.goal.util.Report
import com.planup.planup.goal.util.TmpGoalData
import com.planup.planup.goal.util.loadProfile
import com.planup.planup.goal.util.scaleImageChange
import com.planup.planup.goal.util.toPeriod
import com.planup.planup.main.MainActivity
import com.planup.planup.main.goal.data.GoalRanking
import com.planup.planup.main.goal.item.EditGoalResponse
import com.planup.planup.main.goal.ui.GoalFragment
import com.planup.planup.main.goal.ui.GoalUpdateDialog
import com.planup.planup.network.RetrofitInstance
import com.planup.planup.network.dto.friend.FriendReportRequestDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GoalCommunityJoinFragment : Fragment() {

    companion object { const val ARG_GOAL_ID = "goalId" }

    private lateinit var binding: FragmentGoalCommunityJoinBinding
    private var isPublic: Boolean = true
    private var goalId: Int = -1
    private var popupWindow: PopupWindow? = null
    private var goalData = TmpGoalData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getInt(ARG_GOAL_ID, -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalCommunityJoinBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener { (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GoalFragment()).commitAllowingStateLoss()}

        if (goalId <= 0) {
            Toast.makeText(requireContext(), "잘못된 목표입니다.", Toast.LENGTH_SHORT).show()
        } else {
            loadGoalDetail(goalId)   // ✅ 주석 해제해서 실제 호출
            setRanking(goalId)
            binding.goalJoinNextBtn.setOnClickListener {
                setJoin(goalId)
            }
            binding.editGoalNextBtn.setOnClickListener {
                val goalActivity = requireActivity() as GoalActivity
                Log.d("CommonGoalFragment", "goalType: ${goalActivity.goalType}")
                val goalInputFragment = GoalInputFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("isData",true)
                    }
                }
                (requireActivity() as GoalActivity).navigateToFragment(goalInputFragment)
            }
        }

        return binding.root
    }

    private val editGoalLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoalFragment", "resultCode: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val showDialog = result.data?.getBooleanExtra("SHOW_DIALOG", false) ?: false
            if (showDialog) {
                GoalUpdateDialog().show(parentFragmentManager, "GoalUpdateDialog")
            }
        }
    }

    private fun loadGoalDetail(goalId: Int) {
        lifecycleScope.launch {
            runCatching {
                RetrofitInstance.goalApi.getEditGoal(goalId = goalId) // GoalDetailResponse
            }.onSuccess { resp ->
                if (resp.isSuccessful) {
                    val goal = resp.body()!!.result  // ✅ 타입 맞춤
                    bindGoal(goal)
                } else {
                    Toast.makeText(requireContext(), resp.message(), Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(requireContext(), "목표 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 현재 서버 DTO(GoalResult)에 존재하는 필드만 안전하게 바인딩
    private fun bindGoal(goal: EditGoalResponse) {
        binding.goalTitleTv.text = goal.goalName
        binding.oneDoseTv.text = "목표 1회 량${goal.oneDose} / ${goal.goalAmount}"

        // 아래 값들은 GoalResult에 아직 없으므로 일단 표시 보류/임시값
        binding.periodTv.text = goal.period.toPeriod()
        binding.frequencyTv.text = "기준 기간당 달성 횟수: ${goal.frequency}회"
        binding.verificationTv.text = when (goal.verificationType) {
            "TIMER" -> "타이머 인증"
            "PICTURE" -> "사진 인증"
            else -> "알 수 없음"
        }
        (requireActivity() as GoalActivity).apply {
            goalName = goal.goalName
            goalAmount = goal.goalAmount
            goalType = goal.goalType
            goalCategory = goal.goalCategory
            goalTime = goal.goalTime
            endDate = goal.endDate
            frequency = goal.frequency
            verificationType = goal.verificationType
            period = goal.period
            oneDose = goal.oneDose.toString()
            limitFriendCount = goal.limitFriendCount
        }
    }

    fun bindRanking(ranking: List<GoalRanking>, onUserClick: (Int) -> Unit) {
        binding.apply {
            val empty = ranking.size < 2
            initRaking.visibility = if (empty) View.VISIBLE else View.GONE
            topThirdRaking.visibility = if (empty) View.GONE else View.VISIBLE
            rankRecyclerView.visibility = if (empty) View.GONE else View.VISIBLE
            if (!empty){
                firstRankLayout.setOnClickListener {
                    onUserClick(ranking[0].userId)
                }
                firstProfile.loadProfile(ranking[0].profileImg)
                firstName.text = ranking[0].nickName
                firstVer.text = "사진인증${ranking[0].verificationCount}회"
                secondRankLayout.setOnClickListener {
                    onUserClick(ranking[1].userId)
                }
                secondProfile.loadProfile(ranking[1].profileImg)

                secondName.text = ranking[1].nickName
                secondVer.text = "사진인증${ranking[1].verificationCount}회"
                if (ranking.size < 3) {
                    thirdRankLayout.visibility = View.GONE
                } else {
                    thirdRankLayout.setOnClickListener {
                        onUserClick(ranking[2].userId)
                    }
                    ThirdProfile.loadProfile(ranking[2].profileImg)
                    ThirdName.text = ranking[2].nickName
                    ThirdVer.text = "시진인증${ranking[2].verificationCount}회"

                    val remain = ranking.drop(3)
                    if (remain.isNotEmpty()){
                        rankRecyclerView.adapter = RankURLAdapter(
                            ranking.mapIndexed { index, ranking ->
                                RankURLItem(
                                    index + 3,
                                    ranking.userId,
                                    ranking.nickName,
                                    ranking.verificationCount,
                                    ranking.profileImg
                                )
                            },
                            onUserClick = {userId->
                                onUserClick(userId)
                            }
                        )
                    }
                }
            }
        }

    }

    fun setRanking(goalId: Int) {
        lifecycleScope.launch {
            com.planup.planup.goal.util.setRanking(requireContext(),goalId){
                    ranking -> bindRanking(ranking){ id->
                        userPopup(id)
                }
            }
        }
    }

    fun setJoin(goalId:Int){
        lifecycleScope.launch {
            runCatching {
                RetrofitInstance.goalApi.joinGoal(goalId)
            }.onSuccess { resp ->
                if (resp.isSuccessful && resp.body() != null) {
                    Log.d("join","${resp.body()!!.result}")
                    requireActivity().finish()
                    Toast.makeText(requireContext(), "목표에 참여되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), resp.message(), Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(requireContext(), "목표 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("ResourceType")
    fun userPopup(id:Int){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_user_menu)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(R.color.transparent)
            dialog.window?.decorView?.setPadding(
                30.dp(),
                0,
                30.dp(),
                0
            )
        }

        dialog.findViewById<TextView>(R.id.popup_request_friend).setOnClickListener {

            dialog.dismiss()
        }
        val anchorView = dialog.findViewById<FrameLayout>(R.id.popup_emergency)
        anchorView.setOnClickListener {
            val arrow = dialog.findViewById<ImageView>(R.id.emergency_toggle)
            if (popupWindow?.isShowing == true) {
                arrow.scaleImageChange(R.drawable.ic_arrow_right)
                popupWindow?.dismiss()

                return@setOnClickListener
            }
            arrow.scaleImageChange(R.drawable.ic_arrow_down)
            val popupView = layoutInflater.inflate(
                R.layout.menu_report_dropdown,
                null
            )

            Report.entries.forEach { report->
                val textView = popupView.findViewById<TextView>(report.id)
                textView.setOnClickListener {
                    Log.d("report",report.title)
                    lifecycleScope.launch {
                        runCatching {
                            RetrofitInstance.friendApi.reportFriend(FriendReportRequestDto(id,report.title,true))
                        }.onSuccess { resp ->
                            if (resp.isSuccessful && resp.body() != null) {
                                Log.d("join", "${resp.body()!!.result}")
                                Toast.makeText(requireContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT)
                                    .show()
                                setRanking(goalId)
                                dialog.dismiss()
                            } else {
                                Toast.makeText(requireContext(), resp.message(), Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss()
                            }
                        }

                    }
                }
            }

            popupWindow = PopupWindow(
                popupView,
                anchorView.width,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )



            popupWindow?.apply {
                isOutsideTouchable = true
                isFocusable = true
                isClippingEnabled = false
                setOnDismissListener {
                    arrow.scaleImageChange(R.drawable.ic_arrow_right)
                    popupWindow = null
                }

                showAsDropDown(anchorView)
            }
        }

        dialog.findViewById<TextView>(R.id.popup_inter_pager).setOnClickListener {

        }

        //외부 터치 시 팝업 종료
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    fun Int.dp(): Int =
        (this * resources.displayMetrics.density).toInt()
}