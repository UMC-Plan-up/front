package com.example.planup.main.home.ui

import android.content.Intent
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.planup.main.home.adapter.FriendChallengeAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planup.main.home.item.FriendChallengeItem
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentHomeBinding
import com.example.planup.databinding.ItemCalendarDayBinding
import com.example.planup.main.home.data.DailyToDo
import com.example.planup.main.home.adapter.DailyToDoAdapter
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import com.example.planup.main.home.data.ChallengeReceivedTimer
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import kotlin.coroutines.resume

class HomeFragment : Fragment() {
    private lateinit var prefs: SharedPreferences

    private lateinit var dailyRecyclerVIew: RecyclerView
    private lateinit var dailyAdapter: DailyToDoAdapter
    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendAdapter: FriendChallengeAdapter
    private lateinit var calendarCardView : CardView
    private val today = LocalDate.now()
    private var selectedDate = today
    private var eventList = mutableListOf<CalendarEvent>(
        // 더미 데이터
//        CalendarEvent("토익 공부하기", "DAY", 1, LocalDate.of(2025, 8, 17)),
//        CalendarEvent("헬스장 가기", "DAY", 1, LocalDate.of(2025, 8, 18)),
//        CalendarEvent("스터디 모임", "DAY", 1, LocalDate.of(2025, 8, 19)),
//        CalendarEvent("<인간관계론> 읽기", "DAY", 1, LocalDate.of(2025, 8, 18))
    )
    private var dailyToDos = mutableListOf(
        DailyToDo("공부", 75, 5),
        DailyToDo("독서", 100, 5),
        DailyToDo("운동", 50, 3)
    )
    private var friendChallengingList = mutableListOf(
        FriendChallengeItem(1,"블루", "평균 목표 달성률 : 70%", R.drawable.profile_example_2, listOf(30f, 50f, 70f)),
        FriendChallengeItem(2,"블루", "평균 목표 달성률 : 70%", R.drawable.profile_example_2, listOf(35f, 45f, 65f))
    )

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)
        val nickname = prefs.getString("nickname","")
        val profileImage = prefs.getString("profileImage","")
        binding.homeMainTv.text = "${nickname} 님, 벌써 이만큼이나 왔어요!"
        Glide.with(this)
            .load(profileImage)
            .circleCrop()
            .error(R.drawable.ic_profile)
            .into(binding.homeMainProfileIv)

        //온보딩
        val prefs = requireActivity().getSharedPreferences("haveTutorial", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("haveTutorial", false)) {
            TutorialManager(parentFragmentManager, requireContext()).startTutorial()
            prefs.edit().putBoolean("haveTutorial", true).apply()
        }

        loadMyGoalList(token) //api 불러오기
        viewLifecycleOwner.lifecycleScope.launch {
            val summaries = loadFriendGoalSummaries(token)
            Log.d("FriendGoalSummary","summaries: $summaries")
            // friendChallengingList.clear()
            for (summary in summaries) friendChallengingList.add(summary)
            Log.d("FriendGoalSummary", "최종 summaries: $friendChallengingList")

            friendAdapter = FriendChallengeAdapter(friendChallengingList.take(3)) { item ->
                // 클릭 시 이동 처리
                val fragment = FriendGoalListFragment()
                val bundle = Bundle().apply {
                    putInt("friendId", item.friendId)
                    putString("friendName", item.name)
                    putString("friendResId", item.profileResId.toString())
                }
                fragment.arguments = bundle
                // 예시
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment) // fragment_container는 activity/fragment에 맞게 수정
                    .addToBackStack(null)
                    .commit()
            }
            friendRecyclerView.adapter = friendAdapter
        }

        dailyRecyclerVIew = binding.dailyTodoRv
        dailyRecyclerVIew.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        val progressBar = binding.dailyTodoPb
        progressBar.progress = 75

        friendRecyclerView = binding.homeFriendChallengeRv
        friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        //---------------------달력---------------------
        val calendarView = binding.homeCalendarView
        val monthYearText = binding.homeMonthYearText
        val prevArrow = binding.homeCalendarArrowPrevIv
        val nextArrow = binding.homeCalendarArrowNextIv

        val daysOfWeek = daysOfWeekFromLocale()
        var currentMonth = YearMonth.now()
        calendarView.setup(currentMonth.minusMonths(12), currentMonth.plusMonths(12), daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)


        monthYearText.text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

        calendarView.monthScrollListener = { month ->
            monthYearText.text = month.yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        }

        val startDate = currentMonth.atDay(1)
        val endDate = currentMonth.atEndOfMonth()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            loadDailyGoals(token, currentDate) // 날짜 단위 API 호출
            currentDate = currentDate.plusDays(1)
        }

        prevArrow.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            calendarView.scrollToMonth(currentMonth)
            loadMonthEvents(token, currentMonth)
        }

// 다음 달로 이동
        nextArrow.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            calendarView.scrollToMonth(currentMonth)
            loadMonthEvents(token, currentMonth)
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val date = data.date
                container.textView.text = date.dayOfMonth.toString()

                // 선택 표시
                container.textView.setBackgroundResource(
                    if (date == selectedDate) R.drawable.bg_calendar_select else 0
                )
                container.textView.setTextColor(
                    if (date == selectedDate) ContextCompat.getColor(container.textView.context, R.color.white)
                    else ContextCompat.getColor(container.textView.context, R.color.black_400)
                )

                // 해당 날짜 이벤트 가져오기
                val events = getEventsForDate(date)
                val bars = listOf(container.bar1, container.bar2, container.bar3)
                container.barsContainer.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
                bars.forEach { it.visibility = View.GONE }

                for (i in 0 until minOf(events.size, 3)) {
                    bars[i].visibility = View.VISIBLE
                }

                // 날짜 클릭 시 API 호출
                container.view.setOnClickListener {
                    selectedDate = date
                    calendarView.notifyCalendarChanged()
                    //loadDailyGoals(token = token, date = selectedDate)
                }
            }
        }

        calendarCardView = binding.homeCalendarCardView
        calendarCardView.setOnClickListener {
            val intent = Intent(requireContext(), CalendarActivity::class.java)
            startActivity(intent)
        }

        val fab = binding.homeFab
        Log.d("selectedDate", "selectedDate: ${selectedDate.toString()}")
        fab.setOnClickListener {
            val eventsForDate = getEventsForDate(selectedDate) // 선택한 날짜의 일정 리스트 가져오기

            val bundle = Bundle().apply {
                putString("selectedDate", selectedDate.toString())
            }

            val fragmentTimer = TimerFragment().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragmentTimer)
                .addToBackStack(null)
                .commit()
        }
    }
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val binding = ItemCalendarDayBinding.bind(view)
        val textView: TextView = binding.calendarDayText
        val barsContainer: LinearLayout = binding.eventBarsContainer
        val bar1: View = binding.eventBar1
        val bar2: View = binding.eventBar2
        val bar3: View = binding.eventBar3
    }

    fun daysOfWeekFromLocale(): List<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val days = DayOfWeek.values().toList()
        return days.drop(firstDayOfWeek.ordinal) + days.take(firstDayOfWeek.ordinal)
    }
    private fun clickListener() {
        binding.homeAlarmCl.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeAlertFragment())
                .commitAllowingStateLoss()
        }
        binding.homeMainProfileIv.setOnClickListener {
            showPopup()
        }
    }

    private fun getEventsForDate(date: LocalDate): List<CalendarEvent> {
        return eventList.filter { it.date == date }
    }
    private fun showPopup(){
        val timerChallenge = ChallengeReceivedTimer(
            1,
            16,
            listOf(13L),
            "friend",
            "goalName",
            "goalAmount",
            1200,
            "endDate",
            "duration",
            "frequency",
            "penalty"
        )
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_challenge)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            //팝업에 요청한 친구 이름 바인딩
            dialog.findViewById<TextView>(R.id.popup_challenge_notice_tv).text = getString(R.string.popup_challenge_request,"그린")
            //배경 투명색
            dialog.setCanceledOnTouchOutside(true)
        }
        //닫기 버튼 클릭 시 팝업 종료
        dialog.findViewById<View>(R.id.popup_challenge_close_iv).setOnClickListener {
            dialog.dismiss()
        }
        //확인하러 가기 버튼 클릭 시 챌린지 요청 조회 화면으로 이동
        dialog.findViewById<TextView>(R.id.popup_challenge_btn).setOnClickListener{
            dialog.dismiss()

            //API response에 따라 photo 또는 timer로 전환
            //bundle로 데이터 넘기는 작업도 필요함
            val challengeTimer = ChallengeReceivedTimerFragment()
            challengeTimer.arguments = Bundle().apply {
                putParcelable("receivedChallenge",timerChallenge)
            }
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,challengeTimer)
                .commitAllowingStateLoss()
        }
        dialog.show()
    }

    private suspend fun loadTotalAchievementSuspend(token: String, goalId: Int): Int? {
        return suspendCancellableCoroutine { cont ->
            loadTotalAchievement(token, goalId) { total ->
                cont.resume( total)
            }
        }
    }

    private fun loadMyGoalList(token: String?) {
        if(token == null) return
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getMyGoalList(token = "Bearer $token")
                if (response.isSuccess) {
                    val goals = response.result
                    dailyToDos.clear() // 기존 데이터 초기화
                    for (goal in goals) {
                        val total = loadTotalAchievementSuspend(token, goal.goalId) ?: 0
                        dailyToDos.add(DailyToDo(goal.goalName, total, goal.frequency))
                    }
                    dailyAdapter = DailyToDoAdapter(dailyToDos)
                    dailyRecyclerVIew.adapter = dailyAdapter
                } else {
                    Log.d("HomeFragmentApi","loadMyGoalList 실패 : ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadDailyGoals(token: String?, date: LocalDate) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.goalApi.getDailyGoal(
                    token = "Bearer $token",
                    date = date.toString()
                )

                if (response.isSuccess) {
                    val dailyGoals = response.result?.verifiedGoals ?: emptyList()


                    // API 응답을 CalendarEvent로 변환
                    dailyGoals.forEach { goal ->
                        eventList.add(
                            CalendarEvent(
                                goalName = goal.goalName,
                                period = goal.period ?: "NULL",
                                frequency = goal.frequency,
                                date = date
                            )
                        )
                    }
                    binding.homeCalendarView.notifyCalendarChanged()
                    Log.d("HomeFragmentApi","calendar eventlist : $eventList")
                } else {
                    Log.d("CalendarActivity", "Error: ${response.message}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CalendarActivity", "date : $date, Exception: $e")
            }
        }
    }

    private fun loadMonthEvents(token: String?, month: YearMonth){
        lifecycleScope.launch {
            eventList.clear() // 이전 달 이벤트 제거
            var date = month.atDay(1)
            val endDate = month.atEndOfMonth()
            while (!date.isAfter(endDate)) {
                loadDailyGoals(token, date)
                date = date.plusDays(1)
            }
        }
    }

    private fun loadTotalAchievement(token: String?, goalId: Int, callback: (Int?) -> Unit) {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.goalApi
                val response = apiService.getTotalAchievement("Bearer $token", goalId)
                if (response.isSuccess) {
                    Log.d("loadToTalAchievement", "totalAchievementRate: ${response.result.totalAchievementRate}")
                    callback(response.result.totalAchievementRate)
                } else {
                    Log.d("loadTotalAchievement", "Error: ${response.message}")
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e("loadTotalAchievement", "Error: ${e.message}", e)
                callback(null)
            }
        }
    }

    suspend fun loadFriendGoalSummaries(token: String?): List<FriendChallengeItem> {
        val summaries = mutableListOf<FriendChallengeItem>()

        try {
            // 1. 친구 목록 불러오기
            val friendResponse = RetrofitInstance.friendApi.getFriendSummary("Bearer $token")
            Log.d("FriendGoalSummary", "getFriendSummary response: $friendResponse")

            if (friendResponse.isSuccessful) {
                val friendList = friendResponse.body()?.result ?: emptyList()
                Log.d("FriendGoalSummary", "친구 수: ${friendList.size}")
                val friendInfo = friendList[0].friendInfoSummaryList
                for (friend in friendInfo) {
                    val friendId = friend.id
                    val nickname = friend.nickname
                    Log.d("FriendGoalSummary", "친구 처리 중: id=$friendId, nickname=$nickname")

                    // 2. 친구의 목표 리스트 불러오기
                    val goalService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                    val goalListResponse =
                        goalService.getFriendGoalList("Bearer $token", friendId)
                    Log.d("FriendGoalSummary", "getFriendGoalList(${friendId}) response: $goalListResponse")

                    if (goalListResponse.isSuccess) {
                        val goalList = goalListResponse.result
                        Log.d("FriendGoalSummary", "friendId=$friendId 목표 개수: ${goalList.size}")

                        val achievements = mutableListOf<Int>()

                        // 3. 각 goalId의 달성률 불러오기
                        for (goal in goalList) {
                            try {
                                val achievementResponse =
                                    goalService.getFriendGoalAchievement(
                                        "Bearer $token",
                                        friendId,
                                        goal.goalId
                                    )
                                Log.d(
                                    "FriendGoalSummary",
                                    "getFriendGoalAchievement(friendId=$friendId, goalId=${goal.goalId}) response: $achievementResponse"
                                )

                                if (achievementResponse.isSuccess) {
                                    val achievement = achievementResponse.result.totalAchievement
                                    Log.d(
                                        "FriendGoalSummary",
                                        "달성률 추가: friendId=$friendId, goalId=${goal.goalId}, achievement=$achievement"
                                    )
                                    achievements.add(achievement)
                                } else {
                                    Log.e(
                                        "FriendGoalSummary",
                                        "달성률 실패: friendId=$friendId, goalId=${goal.goalId}, message=${achievementResponse.message}"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "FriendGoalSummary",
                                    "달성률 API 예외: friendId=$friendId, goalId=${goal.goalId}, error=${e.message}"
                                )
                            }
                        }
                        val avg = achievements.average()
                        val top3 = achievements.sortedDescending().take(3)
                        val top3float = top3.map { it.toFloat() }
                        summaries.add(
                            FriendChallengeItem(
                                friendId = friendId,
                                name = nickname,
                                description = "평균 목표 달성률 : $avg",
                                profileResId = R.drawable.profile_example,
                                pieValues = top3float
                            )
                        )

                        Log.d(
                            "FriendGoalSummary",
                            "요약 추가: friendId=$friendId, nickname=$nickname, avg=$avg, top3=$top3"
                        )

                    } else {
                        Log.e(
                            "FriendGoalSummary",
                            "목표 리스트 실패: friendId=$friendId, message=${goalListResponse.message}"
                        )
                    }
                }
            } else {
                Log.e("FriendGoalSummary", "친구 목록 실패: code=${friendResponse.code()}, errorBody=${friendResponse.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("FriendGoalSummary", "전체 처리 예외: ${e.message}")
            e.printStackTrace()
            if(e is HttpException) {
                Log.e("FriendGoalSummary", "HTTP 에러: ${e.code()}, ${e.response()?.errorBody()?.string()}")
            } else {
                Log.e("FriendGoalSummary", "기타 에러: ${e.message}")
            }
        }

        return summaries
    }
}