package com.example.planup.main.home.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentTimerBinding
import com.example.planup.databinding.PopupGoalListCameraBinding
import com.example.planup.main.home.adapter.FriendTimerAdapter
import com.example.planup.main.home.data.FriendTimer
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.Build
import com.example.planup.main.goal.item.GoalRetrofitInstance
import com.example.planup.main.home.data.HomeTimer
import com.example.planup.main.goal.item.GoalApiService
import java.time.LocalDate
import kotlin.collections.plus

class TimerFragment : Fragment() {
    private lateinit var prefs: SharedPreferences
    private var selectedSpinnerItem = 0

    private lateinit var binding: FragmentTimerBinding
    private var timerJob: Job? = null
    private var isRunning = false
    private var elapsedSeconds = 0
    private var events = listOf<HomeTimer>(
        HomeTimer(1, "토익 공부하기"),
        HomeTimer(2, "헬스장 가기"),
        HomeTimer(3, "스터디 모임")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedDate = arguments?.getString("selectedDate")
        val dateTv = binding.goalListTextDateTv

        val formattedDate = selectedDate?.replace("-", ".")
        val prefs = requireContext().getSharedPreferences("userInfo", 0)
        val token = prefs.getString("accessToken", null)

        dateTv.text = formattedDate

        loadMyGoalList(token)

        setupSpinner(token, events)
        setupCameraPopup()
        setupTimerButton(token, selectedSpinnerItem)

        val recyclerView = binding.friendTimerRv

        val examplefriendList = listOf(
            FriendTimer("닉네임", "00:00:00", null),
            FriendTimer("닉네임", "00:00:00", null),
            FriendTimer("닉네임", "00:00:00", null)
        )

        val adapter = FriendTimerAdapter(examplefriendList)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        val backBtn = binding.goalListBackBtn
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupSpinner(token: String?, events: List<HomeTimer>) {
        val spinner: Spinner = binding.goalListSpinner
        val goalNames = events.map { it.goalName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedGoal = events[position]
                selectedSpinnerItem = selectedGoal.goalId
                Toast.makeText(requireContext(), "선택: ${selectedGoal.goalName}", Toast.LENGTH_SHORT).show()

                loadTodayTotalTime(token, selectedGoal.goalId)
                loadFriendsTimer(token, selectedGoal.goalId)

                val prefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
                val savedTimerId = prefs.getInt("timer_${selectedGoal.goalId}", -1)

                if (savedTimerId != -1) {
                    // 타이머 진행 중이면 계속 실행
                    startTimerWithSavedId(token, selectedGoal.goalId, savedTimerId)
                } else {
                    stopTimer() // 없으면 멈춤
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun startTimerWithSavedId(token: String?, goalId: Int, timerId: Int) {
        isRunning = true
        //binding.goalListPlayBtn.setImageResource(R.drawable.ic_pause_circle)

        timerJob?.cancel()
        timerJob = lifecycleScope.launch {
            while (true) {
                delay(1000)
                elapsedSeconds++
                updateTimerText()
            }
        }
    }

    private fun setupCameraPopup() {
        val cameraImageView = binding.goalListBtnCameraIb

        cameraImageView.setOnClickListener {
            val inflater = LayoutInflater.from(requireContext())
            val popupView = inflater.inflate(R.layout.popup_goal_list_camera, null)
            val popupBinding = PopupGoalListCameraBinding.bind(popupView)

            val popupWindow = PopupWindow(
                popupBinding.root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.setBackgroundDrawable(null)
            popupWindow.isOutsideTouchable = true
            popupWindow.elevation = 10f

            val takePhoto = popupBinding.takePhotoTv
            val chooseGallery = popupBinding.chooseGalleryTv

            takePhoto.setOnClickListener {
                Toast.makeText(requireContext(), "사진 찍기 선택", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            chooseGallery.setOnClickListener {
                Toast.makeText(requireContext(), "앨범에서 선택하기 선택", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(cameraImageView, 0, 10)
        }
    }

    private fun setupTimerButton(token: String?, goalId: Int) {
        val playButton = binding.goalListPlayBtn

        playButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer(token, goalId)
            }
        }
    }

    private fun startTimer(token: String?, goalId: Int) {
        isRunning = true
        timerJob?.cancel()

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.verificationApi.postTimerStart(token = "Bearer $token", goalId = goalId)
                if (response.isSuccess) {
                    val timerId = response.result.timerId
                    val prefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
                    prefs.edit().putInt("timer_$goalId", timerId).apply()
                    startTimerWithSavedId(token, goalId, timerId)
                    Log.d("TimerFragmentAPI", "타이머 시작 성공, timerId: $timerId")
                } else {
                    Log.e("TimerFragmentAPI", "타이머 시작 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("TimerFragmentAPI", "에러: ${e.message}")
            }
        }
    }

    private fun stopTimer() {
        isRunning = false
        binding.goalListPlayBtn.setImageResource(R.drawable.ic_play_circle)
        timerJob?.cancel()

        val prefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
        prefs.edit().remove("timer_$selectedSpinnerItem").apply()

        stopTimerApi(token = null, goalId = selectedSpinnerItem) // 필요 시 token 전달
    }

    private fun updateTimerText() {
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60
        binding.goalListTextTimerTv.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerJob?.cancel()
    }

    private fun loadTodayTotalTime(token: String?, goalId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.verificationApi.getTodayTotalTime(token = "Bearer $token", goalId = goalId)
                if (response.isSuccess) {
                    val formattedTime = response.result.formattedTime
                    Log.d("TimerFragmentAPI", "오늘 총 시간: $formattedTime")
                } else {
                    Log.e("TimerFragmentAPI", "실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("TimerFragmentAPI", "에러: ${e.message}")
            }
        }
    }

    private fun startTimerApi(token: String?, goalId: Int){
        lifecycleScope.launch {
            try{
                val response = RetrofitInstance.verificationApi.postTimerStart(token = "Bearer $token", goalId = goalId)
                if (response.isSuccess) {
                    Log.d("TimerFragmentAPI", "타이머 시작 성공")
                } else {
                    Log.e("TimerFragmentAPI", "타이머 시작 실패: ${response.message}")
                }
            } catch (e: Exception){
                Log.e("TimerFragmentAPI", "에러: ${e.message}")
            }
        }
    }

    private fun stopTimerApi(token: String?, goalId: Int) {
        val prefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
        val timerId = prefs.getInt("timer_$goalId", -1)
        if (timerId == -1) return // 타이머가 없으면 호출 안함

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.verificationApi.putTimerStop(token = "Bearer $token", timerId)
                if (response.isSuccess) {
                    Log.d("TimerFragmentAPI", "타이머 중지 성공")
                    // 중지 성공 시 SharedPreferences 삭제
                    prefs.edit().remove("timer_$goalId").apply()
                } else {
                    Log.e("TimerFragmentAPI", "타이머 중지 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("TimerFragmentAPI", "에러: ${e.message}")
            }
        }
    }

    private fun loadFriendsTimer(token: String?, goalId: Int) {
        lifecycleScope.launch {
            try {
                val apiservice = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiservice.getFriendsTimer(
                    token = "Bearer $token",
                    goalId = goalId
                )
                if (response.isSuccess) {
                    val friendList = response.result.map { friend ->
                        FriendTimer(
                            nickname = friend.nickname,
                            time = friend.todayTime,
                            profileResId = friend.profileImg
                        )
                    }

                    val adapter = FriendTimerAdapter(friendList)
                    binding.friendTimerRv.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.friendTimerRv.adapter = adapter

                    Log.d("TimerFragmentAPI", "친구 타이머 불러오기 성공: ${friendList.size}명")
                } else {
                    Log.e("TimerFragmentAPI", "친구 타이머 불러오기 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("TimerFragmentAPI", "친구 타이머 로드 에러: ${e.message}")
            }
        }
    }

    private fun loadMyGoalList(token: String?) {
        if(token == null) {
            Log.d("TimerFragment", "loadMyGoalList token null")
            return
        }
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getMyGoalList(token = "Bearer $token")
                if (response.isSuccess) {
                    val goals = response.result
                    for (goal in goals) {
                        events+HomeTimer(goal.goalId, goal.goalName)
                    }
                } else {
                    Toast.makeText(requireContext(), "목표 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
