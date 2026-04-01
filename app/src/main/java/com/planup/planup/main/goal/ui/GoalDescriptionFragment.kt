package com.planup.planup.main.goal.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.planup.planup.R
import com.planup.planup.databinding.FragmentGoalDescriptionBinding
import com.planup.planup.goal.adapter.CommentAdapter
import com.planup.planup.goal.adapter.CommentItem
import com.planup.planup.goal.adapter.RankURLAdapter
import com.planup.planup.goal.adapter.RankURLItem
import com.planup.planup.goal.data.ReportGoal
import com.planup.planup.goal.util.Report
import com.planup.planup.goal.util.loadProfile
import com.planup.planup.goal.util.scaleImageChange
import com.planup.planup.goal.util.setRanking
import com.planup.planup.goal.util.toPeriod
import com.planup.planup.main.MainActivity
import com.planup.planup.main.goal.data.GoalRanking
import com.planup.planup.main.goal.item.GoalResult
import com.planup.planup.network.RetrofitInstance
import com.planup.planup.network.dto.friend.FriendReportRequestDto
import kotlinx.coroutines.launch


class GoalDescriptionFragment : Fragment() {

    companion object { const val ARG_GOAL_ID = "goalId" }

    private lateinit var binding: FragmentGoalDescriptionBinding
    private var isPublic: Boolean = true
    private var goalId: Int = -1
    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getInt(ARG_GOAL_ID, -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalDescriptionBinding.inflate(inflater, container, false)

        applyToggleUI(isPublic)

        binding.btnBack.setOnClickListener { (requireContext() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GoalFragment()).commitAllowingStateLoss()}
        binding.reportBar.setOnClickListener {v ->
            initPopup()
            popupWindow?.let {
                if (it.isShowing) it.dismiss()
                else it.showAsDropDown(v)
            }
        }
        binding.btnPublic.setOnClickListener {
            if (!isPublic) { isPublic = true; applyToggleUI(isPublic) }
        }
        binding.btnPrivate.setOnClickListener { if (isPublic) showPrivateDialog() }

        binding.inviteFriend.setOnClickListener {

        }

        if (goalId <= 0) {
            Toast.makeText(requireContext(), "잘못된 목표입니다.", Toast.LENGTH_SHORT).show()
        } else {
            loadGoalDetail(goalId)   // ✅ 주석 해제해서 실제 호출
            setRanking(goalId)
        }

        binding.goalDescEditIv.setOnClickListener {
            val intent = Intent(requireContext(), EditFriendGoalActivity::class.java)
            intent.putExtra("goalId",goalId)
            editGoalLauncher.launch(intent)
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
                RetrofitInstance.goalApi.getGoalDetail(goalId = goalId) // GoalDetailResponse
            }.onSuccess { resp ->
                if (resp.isSuccess) {
                    val goal: GoalResult = resp.result  // ✅ 타입 맞춤
                    bindGoal(goal)
                } else {
                    Toast.makeText(requireContext(), resp.message, Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(requireContext(), "목표 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 현재 서버 DTO(GoalResult)에 존재하는 필드만 안전하게 바인딩
    private fun bindGoal(goal: GoalResult) {
        binding.goalTitleTv.text = goal.goalName
        binding.oneDoseTv.text = "${goal.goalAmount}"

        // 아래 값들은 GoalResult에 아직 없으므로 일단 표시 보류/임시값
        binding.periodTv.text = goal.period.toPeriod()
        binding.frequencyTv.text = "${goal.frequency}회"
        binding.verificationTv.text = when (goal.verificationType) {
            "TIMER" -> "타이머 인증"
            "PICTURE" -> "사진 인증"
            else -> "알 수 없음"
        }

        // 참여자 수 문구가 "%d명 참여중" 형식이면 strings.xml에
        // <string name="goal_description_attendee_cnt_fmt">%1$d명 참여중</string>
        // 로 추가하고 아래처럼 사용하세요. (현재는 데이터가 없으니 임시 0)
        // binding.goal_description_attendee_cnt_tv.text =
        //     getString(R.string.goal_description_attendee_cnt_fmt, 0)

        isPublic = goal.public
        applyToggleUI(isPublic)

        binding.commentRecyclerView.adapter = CommentAdapter(goal.commentList?.map{
            CommentItem(
                it.writer.profileImg,
                it.writer.nickname,
                it.content
            )
        } ?: emptyList())
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

    private fun applyToggleUI(publicSelected: Boolean) {
        binding.btnPublic.isSelected = publicSelected
        binding.btnPrivate.isSelected = !publicSelected
        binding.btnPublic.setTextColor(if (publicSelected) Color.WHITE else Color.BLACK)
        binding.btnPrivate.setTextColor(if (publicSelected) Color.BLACK else Color.WHITE)
    }

    private fun showPrivateDialog() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_private_goal)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        dialog.findViewById<ImageView>(R.id.popup_block_no_iv)?.setOnClickListener { dialog.dismiss() }
        dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)?.setOnClickListener {
            isPublic = false
            applyToggleUI(isPublic)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initPopup() {
        if (popupWindow != null) return

        val popupView = layoutInflater.inflate(R.layout.report_bar, null)

        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable())
        }

        // 👉 popup 내부 뷰 접근은 반드시 popupView 기준
        val parent = popupView.findViewById<LinearLayout>(R.id.menu_parent)
        val mainMenu = popupView.findViewById<LinearLayout>(R.id.mainMenu)
        val subMenu = popupView.findViewById<View>(R.id.toggle_menu)
        val sub = popupView.findViewById<FrameLayout>(R.id.presenter_report)
        Report.entries.forEach { report ->
            val reason = sub.findViewById<TextView>(report.id)
            reason.setOnClickListener {
                lifecycleScope.launch {
                    runCatching {
                        RetrofitInstance.goalApi.reportGoal(goalId, ReportGoal(report.title))
                    }.onSuccess { resp ->
                        if (resp.isSuccessful && resp.body() != null) {
                            Log.d("join", "${resp.body()!!.result}")
                            Toast.makeText(requireContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(requireContext(), resp.message(), Toast.LENGTH_SHORT)
                                .show()

                        }
                    }.onFailure {

                    }
                }
                Log.d("report",it.toString())
            }
        }

        parent.setOnClickListener {
            subMenu.visibility = View.VISIBLE

            // 메인 왼쪽으로 밀림
            mainMenu.animate()
                .translationX(-mainMenu.width.toFloat())
                .alpha(0f)
                .setDuration(200)

            // 서브 오른쪽에서 들어옴
            subMenu.translationX = mainMenu.width.toFloat()
            subMenu.alpha = 0f

            subMenu.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(200)
                .withEndAction {
                    mainMenu.visibility = View.GONE
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

    fun setRanking(goalId: Int) {
        lifecycleScope.launch {
            com.planup.planup.goal.util.setRanking(requireContext(),goalId){
                ranking -> bindRanking(ranking){id->
                    userPopup(id)
            }
            }
        }
    }


    fun Int.dp(): Int =
        (this * resources.displayMetrics.density).toInt()
}