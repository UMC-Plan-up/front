package com.example.planup.main.goal.ui

import android.annotation.SuppressLint
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
import com.example.planup.R
import com.example.planup.database.TokenSaver
import com.example.planup.databinding.FragmentGoalBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.main.MainActivity
import com.example.planup.main.goal.adapter.GoalAdapter
import com.example.planup.main.goal.adapter.GoalApi
import com.example.planup.main.goal.adapter.MyGoalListDtoAdapter
import com.example.planup.main.goal.data.GoalType
import com.example.planup.main.goal.data.MyGoalListDto
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.goal.item.GoalRetrofitInstance
import com.example.planup.network.RetrofitInstance
import com.example.planup.network.controller.GoalController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate

class GoalFragment : Fragment(), MyGoalListDtoAdapter {
    private lateinit var prefs : SharedPreferences
    lateinit var binding: FragmentGoalBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private lateinit var dailyPieChart: PieChart
//    private lateinit var userController: UserController
    private lateinit var goalController: GoalController
    private var isEditMode: Boolean = false

    companion object {
        private const val ARG_TARGET_USER_ID = "TARGET_USER_ID"
        private const val ARG_TARGET_NICKNAME = "TARGET_NICKNAME"

        fun newInstance(targetUserId: Int, targetNickname: String) = GoalFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_TARGET_USER_ID, targetUserId)
                putString(ARG_TARGET_NICKNAME, targetNickname)
            }
        }
    }

    lateinit var tokenSaver : TokenSaver

    private var targetUserIdArg: Int = -1
    private var targetNicknameArg: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalBinding.inflate(inflater, container, false)

//        userController = UserController()
        goalController = GoalController()
        // 사용자 정보 콜백 연결

//        userController.setUserInfoAdapter(object : UserInfoAdapter {
//            override fun successUserInfo(user: UserInfo){
//                // onCreateView() 또는 onViewCreated() 초반
//                val targetUserId = arguments?.getInt(ARG_TARGET_USER_ID, -1) ?: -1
//                val targetNickname = arguments?.getString(ARG_TARGET_NICKNAME).orEmpty()
//
//                if (targetUserId > 0) {
//                    // ✅ 친구 보기 모드: 타이틀만 우선 변경
//                    binding.userGoalListTv.text = "${targetNickname}의 목표 리스트"
//                    // (추가 계획) 친구 목표 조회 API 연동 시 여기서 targetUserId 사용
//                } else {
//                    userController.userInfoService() // 기존 내 정보 로딩
//                }
//            }
//            override fun failUserInfo(message: String){
//                binding.userGoalListTv.text = "내 목표 리스트"
//                loadProfileInto(null)
//            }
//        })
//        userController.userInfoService()99063f

        // 나의 목표 리스트 콜백 연결
        goalController.setMyGoalListAdapter(this)
        // 최초 진입 시 1회 로드
        // goalController.fetchMyGoals()
        targetUserIdArg = arguments?.getInt(ARG_TARGET_USER_ID, -1) ?: -1
        targetNicknameArg = arguments?.getString(ARG_TARGET_NICKNAME).orEmpty()


        clickListener()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenSaver = TokenSaver(requireContext())
        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = tokenSaver.safeToken()
        val nickname = prefs.getString("nickname","사용자")?.removeSurrounding("\"") ?: "사용자"
        Log.d("GoalFragment","token: $token")
        loadMyGoalList(token)

        binding.userGoalListTv.text = "${nickname?.removeSurrounding("\"")}의 목표 리스트"

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
                startActivity(intent)
                //editGoalLauncher.launch(intent)
            },
            onDeactivateConfirmed = { goalId -> requestToggleActive(goalId, willActivate = false) },
            onActivateConfirmed = { goalId -> requestToggleActive(goalId, willActivate = true) },
            onDeleteConfirmed = {goalId -> requestDeleteGoal(goalId)}
        )
        recyclerView.adapter = adapter

        if (targetUserIdArg > 0) {
            // ✅ 친구 모드
            binding.userGoalListTv.text = "${targetNicknameArg}의 목표 리스트"

            // 편집/삭제 같은 본인 전용 UI는 숨기는 걸 권장
            binding.manageButton.visibility = View.GONE
            // (친구 달성률 UI도 없다면 숨겨도 됨)
            // binding.dailyGoalCompleteGroup.isVisible = false  // 레이아웃 구조에 맞춰 선택

            loadFriendGoalList(token, targetUserIdArg)
        } else {
            // ✅ 내 모드
            binding.userGoalListTv.text = "${nickname}의 목표 리스트"
            loadMyGoalList(token)
            loadTodayAchievement(token)
        }

    }
    // ⬇️ 새로 추가
    private fun loadFriendGoalList(token: String?, friendUserId: Int) {
        if (token.isNullOrBlank()) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                // GoalApi 는 네가 준 인터페이스(아래에 있음). 이미 RetrofitInstance.goalApi 쓰고 있으니 동일 사용.
                val res = RetrofitInstance.goalApi.getFriendGoalList(
                    token = token,
                    friendId = friendUserId
                )

                // ⚠️ 서버 응답 데이터 클래스에 오타: 'reuslt'
                if (res.isSuccess) {
//                    val friendGoals = res.result
//                    val items = friendGoals.toGoalItemsForFriend()
//                    setGoals(items)
                } else {
                    Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("GoalFragment", "loadFriendGoalList error", e)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 파일 상단 import에 추가
// import com.example.planup.main.friend.data.FriendGoalListDto
// import com.example.planup.main.friend.data.VerificationType
// import com.example.planup.main.friend.data.GoalType as FriendGoalType

    // ⬇️ 새로 추가: 친구 리스트 변환
    private fun List<com.example.planup.main.friend.data.FriendGoalListDto>.toGoalItemsForFriend(): List<GoalItem> =
        map { dto ->
            val typeLabel = when (dto.goalType) {
                com.example.planup.main.friend.data.GoalType.FRIEND -> "매일"
                com.example.planup.main.friend.data.GoalType.COMMUNITY -> "매주"
                com.example.planup.main.friend.data.GoalType.CHALLENGE_PHOTO -> "매일"
                com.example.planup.main.friend.data.GoalType.CHALLENGE_TIME -> "매주"
            }
            val criteria = "$typeLabel ${dto.frequency}번 이상"
            val auth = when (dto.verificationType) {
                com.example.planup.main.friend.data.VerificationType.PHOTO -> "camera"
                com.example.planup.main.friend.data.VerificationType.TIMER -> "timer"
            }

            GoalItem(
                goalId      = dto.goalId,
                title       = dto.goalName,
                description = criteria,
                percent     = 0,           // 친구용 응답에 퍼센트가 없으므로 0으로 표기 (있으면 반영)
                authType    = auth,
                isEditMode  = false,
                isActive    = true,        // 친구 데이터에 공개/활성 여부가 없으니 true로 표시 (필요 시 서버 필드 연결)
                criteria    = criteria,
                progress    = 0            // 필요 시 서버 값 매핑
            )
        }

    private val goalEditApi by lazy{
        GoalRetrofitInstance.api.create(GoalApi::class.java)
    }

    // 액세스 토큰 가져오기
    private fun requireTokenOrNull(): String?{
        return tokenSaver.safeToken()
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
                GoalType.FRIEND -> "매일"
                GoalType.COMMUNITY -> "매주"
                GoalType.CHALLENGE_PHOTO -> "매일"
                GoalType.CHALLENGE_TIME -> "매주"
            }
            val criteria = "$typeLabel ${dto.frequency}번 이상"

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
                putExtra("TO_CHALLENGE_FROM","GoalFragment")
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
                val response = apiService.getMyGoalList(token = token)
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
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
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
                binding.userProfileIv.setImageResource(R.drawable.ic_profile)
            }
        } else {
            binding.userProfileIv.setImageResource(R.drawable.ic_profile)
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
                val today = LocalDate.now() // yyyy-MM-dd

                val response = apiService.getDailyAchievement(
                    token = token,
                    targetDate = today.toString()
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

    override fun successMyGoals(goals: List<MyGoalListDto>) {
        val items = goals.toGoalItems()
        setGoals(items)
    }

    override fun failMyGoals(message: String) {
        Toast.makeText(requireContext(), "목표 목록을 불러오지 못했습니다: $message", Toast.LENGTH_SHORT).show()
    }
}
