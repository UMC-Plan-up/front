package com.example.planup.main.goal.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.ui.GoalCategoryFragment
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.goal.item.GoalAdapter
import com.example.planup.main.home.adapter.UserInfoAdapter
import com.example.planup.network.controller.UserController
import com.example.planup.network.data.UserInfo
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.planup.network.controller.GoalController
import com.example.planup.main.goal.adapter.MyGoalListDtoAdapter
import com.example.planup.main.goal.data.MyGoalListDto
import com.example.planup.main.goal.data.GoalType

class GoalFragment : Fragment() {
    lateinit var binding: FragmentGoalBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private lateinit var dailyPieChart: PieChart

    private val userController = UserController()
    private val goalController = GoalController()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalBinding.inflate(inflater, container, false)

        // 사용자 정보 콜백 연결
        userController.setUserInfoAdapter(object : UserInfoAdapter {
            override fun successUserInfo(user: UserInfo){
                binding.userGoalListTv.text = "${user.nickname}의 목표 리스트"
                loadProfileInto(user.profileImage)
            }
            override fun failUserInfo(message: String){
                binding.userGoalListTv.text = "내 목표 리스트"
                loadProfileInto(null)
            }
        })
        userController.userInfoService()

        // 나의 목표 리스트 콜백 연결
        goalController.setMyGoalListAdapter(object : MyGoalListDtoAdapter {
            override fun successMyGoals(goals: List<MyGoalListDto>) {
                val items = goals.toGoalItems()
                setGoals(items)
            }
            override fun failMyGoals(message: String) {
                Toast.makeText(requireContext(), "목표 목록을 불러오지 못했습니다: $message", Toast.LENGTH_SHORT).show()
            }
        })
        // 최초 진입 시 1회 로드
        goalController.fetchMyGoals()

        clickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dailyPieChart = binding.dailyGoalCompletePc
        setupPieChart(dailyPieChart, 70)

        recyclerView = binding.goalListRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 초기 빈 리스트
        adapter = GoalAdapter(
            emptyList(),
            onItemClick = { goalItem ->
                val fragment = GoalDescriptionFragment()
                fragment.arguments = Bundle().apply { /* 필요 시 데이터 전달 */ }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onEditClick = { goalId ->
                val intent = Intent(requireContext(), EditFriendGoalActivity::class.java)
                intent.putExtra("goalId", goalId)
                editGoalLauncher.launch(intent)
                Log.d("GoalFragment", "goalId: $goalId")
            },
            onDeactivateConfirmed = { showDeactivateToast() },
            onActivateConfirmed = { showActivateToast() },
            onDeleteConfirmed = { showDeleteToast() }
        )
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // 필요하다면 조건부로
        // if (shouldRefresh) refreshGoals()
    }

    /** 서버 DTO → 화면용 GoalItem으로 변환 */
    private fun List<MyGoalListDto>.toGoalItems(): List<GoalItem> =
        map { dto ->
            val typeLabel = when (dto.goalType) {
                GoalType.FRIEND -> "친구"
                GoalType.COMMUNITY -> "커뮤니티"
                GoalType.CHALLENGE_PHOTO -> "사진 인증"
                GoalType.CHALLENGE_TIME -> "시간 인증"
            }
            val criteria = "$typeLabel · 빈도 ${dto.frequency}회 · 1회 기준 ${dto.oneDose}"

            // 서버에 진행률/퍼센트가 없으므로 기본값 0으로 시작
            val percent = 0
            val progress = 0
            val description = criteria
            val authType = when (dto.goalType) {
                GoalType.CHALLENGE_PHOTO -> "camera"
                GoalType.CHALLENGE_TIME  -> "timer"
                else -> "none"
            }

            GoalItem(
                goalId     = dto.goalId,
                title      = dto.goalName.orEmpty(),
                description= description,
                percent    = percent,
                authType   = authType,
                isActive   = true,     // 서버 플래그가 있으면 반영
                isEditMode = false,    // UI 상태
                criteria   = criteria,
                progress   = progress
            )
        }

    /** RecyclerView에 아이템 반영 */
    private fun setGoals(items: List<GoalItem>) {
        // GoalAdapter에 데이터 갱신 메서드가 없다면 인스턴스 교체
        adapter = GoalAdapter(
            items,
            onItemClick = { goalItem ->
                val fragment = GoalDescriptionFragment()
                fragment.arguments = Bundle().apply { /* 필요 시 데이터 전달 */ }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onEditClick = { goalId ->
                val intent = Intent(requireContext(), EditFriendGoalActivity::class.java)
                intent.putExtra("goalId", goalId)
                editGoalLauncher.launch(intent)
                Log.d("GoalFragment", "goalId: $goalId")
            },
            onDeactivateConfirmed = { showDeactivateToast() },
            onActivateConfirmed = { showActivateToast() },
            onDeleteConfirmed = { showDeleteToast() }
        )
        recyclerView.adapter = adapter
    }

    private fun showDeactivateToast() {
        val layout = layoutInflater.inflate(R.layout.toast_goal_deactivate, binding.root, false)
        Toast(requireContext()).apply {
            view = layout
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        }.show()
    }

    private fun showActivateToast(){
        val layout = layoutInflater.inflate(R.layout.toast_goal_activate, binding.root, false)
        Toast(requireContext()).apply {
            view = layout
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        }.show()
    }

    private fun showDeleteToast(){
        val layout = layoutInflater.inflate(R.layout.toast_goal_delete, binding.root, false)
        Toast(requireContext()).apply {
            view = layout
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        }.show()
    }

    private fun setupPieChart(pieChart: PieChart, progress: Int) {
        val entries = listOf(
            PieEntry(progress.toFloat()),
            PieEntry((100 - progress).toFloat())
        )
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.WHITE, Color.rgb(220, 220, 220))
            setDrawValues(false)
            sliceSpace = 2f
        }
        pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false)
            setTouchEnabled(false)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 70f
            transparentCircleRadius = 0f
            centerText = ""
            invalidate()
        }
    }

    private fun clickListener() {
        binding.manageButton.setOnClickListener {
            val isEditMode = adapter.toggleEditMode()
            binding.manageButton.text = if (isEditMode) "완료" else "관리"
        }
        binding.unlockTextLl.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, SubscriptionPlanFragment())
                .commitAllowingStateLoss()
        }

        binding.lockCircleIv2.setOnClickListener {
            startActivity(Intent(requireContext(), GoalActivity::class.java))
        }
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

    private fun loadProfileInto(urlOrBase64: String?){
        if(!urlOrBase64.isNullOrBlank() && !urlOrBase64.startsWith("data:image")){
            Glide.with(this)
                .load(urlOrBase64)
                .placeholder(R.drawable.img_profile_green)
                .error(R.drawable.img_profile_green)
                .circleCrop()
                .into(binding.userProfileIv)
            return
        }
        if (!urlOrBase64.isNullOrBlank() && urlOrBase64.startsWith("data:image")) {
            val base64 = urlOrBase64.substringAfter(",")
            runCatching {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                binding.userProfileIv.setImageBitmap(bmp)
            }.onFailure {
                binding.userProfileIv.setImageResource(R.drawable.img_profile_green)
            }
        } else {
            binding.userProfileIv.setImageResource(R.drawable.img_profile_green)
        }
    }
}