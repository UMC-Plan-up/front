package com.planup.planup.main.goal.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.main.goal.ui.GoalFragment
import com.planup.planup.R
import com.planup.planup.databinding.FragmentGoalDescriptionBinding
import com.planup.planup.goal.util.toPeriod
import com.planup.planup.main.MainActivity
import com.planup.planup.main.goal.data.GoalRanking
import com.planup.planup.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.lang.reflect.Modifier.isPublic

class GoalDescriptionFragment : Fragment() {

    companion object { const val ARG_GOAL_ID = "goalId" }

    private lateinit var binding: FragmentGoalDescriptionBinding
    private var isPublic: Boolean = true
    private var goalId: Int = -1

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

        binding.btnBack.setOnClickListener { (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GoalFragment()).commitAllowingStateLoss()}
        binding.btnPublic.setOnClickListener {
            if (!isPublic) { isPublic = true; applyToggleUI(isPublic) }
        }
        binding.btnPrivate.setOnClickListener { if (isPublic) showPrivateDialog() }

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
                    val goal: com.planup.planup.main.goal.item.GoalResult = resp.result  // ✅ 타입 맞춤
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
    private fun bindGoal(goal: com.planup.planup.main.goal.item.GoalResult) {
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

        // 참여자 수 문구가 "%d명 참여중" 형식이면 strings.xml에
        // <string name="goal_description_attendee_cnt_fmt">%1$d명 참여중</string>
        // 로 추가하고 아래처럼 사용하세요. (현재는 데이터가 없으니 임시 0)
        // binding.goal_description_attendee_cnt_tv.text =
        //     getString(R.string.goal_description_attendee_cnt_fmt, 0)

        isPublic = goal.public
        applyToggleUI(isPublic)
    }

    fun bindRanking(ranking: List<Pair<Int, GoalRanking>>) {
        binding.apply {
            val empty = ranking.isEmpty()
            initRaking.visibility = if (empty) View.VISIBLE else View.GONE
            topThirdRaking.visibility = if (empty) View.GONE else View.VISIBLE
            rankRecyclerView.visibility = if (empty) View.GONE else View.VISIBLE
            if (ranking.size < 3) {
                firstProfile
                firstName
                firstVer
                secondProfile
                secondName
                secondVer
                ThirdProfile
                ThirdName
                ThirdVer
            } else {

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

    fun setRanking(goalId: Int) {
        lifecycleScope.launch {
            runCatching {
                RetrofitInstance.goalApi.getGoalRanking(goalId = goalId) // GoalDetailResponse
            }.onSuccess { resp ->
                if (resp.isSuccessful && resp.body() != null) {
                    val ranking = resp.body()!!.result.goalRankingList.sortedByDescending { it.verificationCount }
                        .mapIndexed { index, goalRanking ->
                            index + 1 to goalRanking
                        }// ✅ 타입 맞춤
                    bindRanking(ranking)
                } else {
                    Toast.makeText(requireContext(), resp.message(), Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(requireContext(), "목표 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}