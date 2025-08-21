package com.example.planup.main.goal.ui

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.goal.adapter.GoalAdapter
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import com.example.planup.main.home.adapter.UserInfoAdapter
import com.example.planup.network.controller.UserController
import com.example.planup.network.data.UserInfo
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import com.example.planup.network.controller.GoalController
import com.example.planup.main.goal.adapter.MyGoalListDtoAdapter
import com.example.planup.main.goal.data.MyGoalListDto
import com.example.planup.main.goal.data.GoalType
import com.example.planup.network.RetrofitInstance
import retrofit2.HttpException
import java.time.LocalDate
import com.example.planup.main.goal.adapter.GoalApi

class GoalFragment : Fragment() {
    private lateinit var prefs : SharedPreferences
    lateinit var binding: FragmentGoalBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private lateinit var dailyPieChart: PieChart
    private val userController = UserController()
    private val goalController = GoalController()
    private var isEditMode: Boolean = false

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
        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)
        Log.d("GoalFragment","token: $token")
        loadMyGoalList(token)

        dailyPieChart = binding.dailyGoalCompletePc
        loadTodayAchievement(token)

        recyclerView = binding.goalListRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = GoalAdapter(
            emptyList(),
            onItemClick = { goalItem ->
                val fragment = GoalDescriptionFragment().apply {
                    arguments = Bundle().apply {
                        putInt(GoalDescriptionFragment.ARG_GOAL_ID, goalItem.goalId)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onEditClick = { goalId ->
                val intent = Intent(requireContext(), EditFriendGoalActivity::class.java)
                intent.putExtra("goalId",goalId)
                editGoalLauncher.launch(intent)
            },
            onDeactivateConfirmed = { goalId -> requestToggleActive(goalId, willActivate = false) },
            onActivateConfirmed = { goalId -> requestToggleActive(goalId, willActivate = true) },
            onDeleteConfirmed = {goalId -> requestDeleteGoal(goalId)}
        )
        recyclerView.adapter = adapter

    }

    private val goalEditApi by lazy{
        GoalRetrofitInstance.api.create(GoalApi::class.java)
    }

    // 액세스 토큰 가져오기
    private fun requireTokenOrNull(): String?{
        if(!::prefs.isInitialized) return null
        val token = prefs.getString("accessToken", null) ?: return null
        return "Bearer $token"
    }

    // 삭제
    /** 삭제 */
    private fun requestDeleteGoal(goalId: Int) {
        val token = requireTokenOrNull() ?: run {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            runCatching {
                goalEditApi.deleteGoal(token = token, goalId = goalId)
            }.onSuccess { res ->
                if (res.isSuccessful && (res.body()?.isSuccess == true)) {
                    showDeleteToast()
                    // 1) 즉시 UI에서 제거
                    adapter.removeItemById(goalId)
                    // 2) 서버 최신 상태도 재조회(편집모드 유지됨)
                    goalController.fetchMyGoals()
                } else {
                    Toast.makeText(requireContext(),
                        "삭제 실패: ${res.body()?.message ?: res.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.w("GoalFragment", "deleteGoal fail body=${res.body()} code=${res.code()}")
                }
            }.onFailure {
                Toast.makeText(requireContext(), "삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("GoalFragment", "deleteGoal error", it)
            }
        }
    }

    /** 활성/비활성 토글 (이번 클릭 의도 전달) */
    private fun requestToggleActive(goalId: Int, willActivate: Boolean) {
        val token = requireTokenOrNull() ?: return Toast.makeText(requireContext(),"로그인이 필요합니다.",Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            runCatching { goalEditApi.setGoalActive(token, goalId) }
                .onSuccess { res ->
                    if (res.isSuccessful && (res.body()?.isSuccess == true)) {
                        if (willActivate) showActivateToast() else showDeactivateToast()

                        // 1) 즉시 해당 아이템만 UI 반영
                        adapter.updateItemActive(goalId, willActivate)

                        // 2) 정확성 위해 서버 리스트 재조회(옵션)
                        goalController.fetchMyGoals()
                    } else {
                        Toast.makeText(requireContext(),
                            "활성 상태 변경 실패: ${res.body()?.message ?: res.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .onFailure {
                    Toast.makeText(requireContext(),"활성 상태 변경 중 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                    Log.e("GoalFragment","setGoalActive error", it)
                }
        }
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

            GoalItem(
                goalId     = dto.goalId,
                title      = dto.goalName.orEmpty(),
                description= criteria,
                percent    = 0,
                authType   = when (dto.goalType) {
                    GoalType.CHALLENGE_PHOTO -> "camera"
                    GoalType.CHALLENGE_TIME  -> "timer"
                    else -> "none"
                },
                isEditMode = false,
                isActive = !dto.isActive,
                criteria   = criteria,
                progress   = 0
            )
        }

    private fun setGoals(items: List<GoalItem>) {
        adapter.updateItems(items)        // ✅ 리스트만 교체
        adapter.setEditMode(isEditMode)   // 편집 모드 유지
        binding.manageButton.text = if (isEditMode) "완료" else "관리"
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
            isEditMode = !isEditMode
            adapter.setEditMode(isEditMode)  // 어댑터에 반영
            binding.manageButton.text = if (isEditMode) "완료" else "관리"
        }

        // unlockTextLl 클릭 시 SubscriptionPlanFragment로 이동
        binding.unlockTextLl.setOnClickListener {
            val subscriptionFragment = SubscriptionPlanFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("IS_FROM_GOAL_FRAGMENT", true)
                }
            }
            // MainActivity의 navigateToFragment를 사용해 전환
            (context as? MainActivity)?.navigateToFragment(subscriptionFragment)
        }

        binding.lockCircleIv2.setOnClickListener {
            // SharedPreferences에서 닉네임 가져오기
            val nickname = prefs.getString("nickname", "사용자") ?: "사용자"

            val intent = Intent(requireContext(), GoalActivity::class.java).apply {
                putExtra("goalOwnerName", nickname)
            }
            startActivity(intent)
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

    private fun loadMyGoalList(token: String?) {
        if(token == null) {
            Log.d("GoalFragment", "loadMyGoalList token null")
            return
        }
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getMyGoalList(token = "Bearer $token")
                if (response.isSuccess) {
                    val goals = response.result
                    for (goal in goals) {
                        Log.d("GoalFragmentApi","Goal: ${goal.goalName} / type: ${goal.goalType}")
                        goals+GoalItem(goal.goalId, goal.goalName, goal.goalType, percent = 82, criteria = "PHOTO", progress = 82)
                    }
                } else {
                    Log.d("GoalFragmentApi","loadMyGoalList 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("GoalFragmentApi","네트워크 오류")
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

    private fun loadTodayAchievement(token: String?) {
        if(token.isNullOrEmpty()) {
            Log.e("loadTodayAchievement", "Token is null or empty")
            return
        }

        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.goalApi
                val today = LocalDate.now().toString() // yyyy-MM-dd

                val response = apiService.getDailyAchievement(
                    token = "Bearer $token",
                    targetDate = today
                )

                if (response.isSuccess) {
                    val achievementRate = response.result.achievementRate
                    binding.dailyGoalCompletePercentTv.text = "$achievementRate%"
                    setupPieChart(dailyPieChart, achievementRate)
                } else {
                    Log.d("GoalFragmentApi", "loadTodayAchievement 실패: ${response.message}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                if (e is HttpException) {
                    Log.e("todayachievement", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
                } else {
                    Log.e("todayachievement", "Other error: ${e.message}", e)
                }
            }
        }
    }
}
