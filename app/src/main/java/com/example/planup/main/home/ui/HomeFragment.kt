package com.example.planup.main.home.ui

import android.content.Intent
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import com.example.planup.main.home.adapter.FriendChallengeAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
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
import com.example.planup.main.friend.data.FriendInfo
import com.example.planup.main.goal.ui.ChallengeAlertFragment
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
import com.example.planup.main.home.data.HomeTimer
import com.example.planup.main.home.adapter.CalendarEventAdapter
import com.example.planup.main.home.data.ChallengeReceivedTimer
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch

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
        CalendarEvent("토익 공부하기", "DAY", 1, LocalDate.of(2025, 8, 17)),
        CalendarEvent("헬스장 가기", "DAY", 1, LocalDate.of(2025, 8, 18)),
        CalendarEvent("스터디 모임", "DAY", 1, LocalDate.of(2025, 8, 19)),
        CalendarEvent("<인간관계론> 읽기", "DAY", 1, LocalDate.of(2025, 8, 18))
    )
    private var dailyToDos = listOf(
        DailyToDo("공부", 75, 5),
        DailyToDo("독서", 100, 5),
        DailyToDo("운동", 50, 3)
    )
    private var dummyData = listOf(
        FriendChallengeItem("블루", "평균 목표 달성률 : 70%", R.drawable.ic_launcher_background, listOf(30f, 50f, 70f)),
        FriendChallengeItem("블루", "평균 목표 달성률 : 70%", R.drawable.ic_launcher_background, listOf(35f, 45f, 65f))
    )

    private lateinit var friendList : List<FriendInfo>

    private lateinit var binding: FragmentHomeBinding
    companion object {
        private var tutorialshownflag = false
    }
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
//        val prefs = requireActivity().getSharedPreferences("haveTutorial", Context.MODE_PRIVATE)
//        if (!prefs.getBoolean("haveTutorial", false)) {
//            TutorialManager(parentFragmentManager).startTutorial()
//            prefs.edit().putBoolean("haveTutorial", true).apply()
//        }
        if(!tutorialshownflag) {
            TutorialManager(parentFragmentManager).startTutorial()
            tutorialshownflag = true
        }


        loadMyGoalList(token) //api 불러오기
        getMyWeeklyReport(token)
        loadFriendsList(token)


        dailyRecyclerVIew = binding.dailyTodoRv
        dailyRecyclerVIew.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)



        dailyAdapter = DailyToDoAdapter(dailyToDos)
        dailyRecyclerVIew.adapter = dailyAdapter

        val progressBar = binding.dailyTodoPb
        progressBar.progress = 75

        friendRecyclerView = binding.homeFriendChallengeRv
        friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())



        friendAdapter = FriendChallengeAdapter(dummyData) { item ->
            // 클릭 시 이동 처리
            val fragment = FriendGoalListFragment().apply {
                arguments = bundleOf("friendId" to item.name)
            }
            // 예시
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment) // fragment_container는 activity/fragment에 맞게 수정
                .addToBackStack(null)
                .commit()
        }
        friendRecyclerView.adapter = friendAdapter

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

        prevArrow.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            calendarView.scrollToMonth(currentMonth)
        }

// 다음 달로 이동
        nextArrow.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            calendarView.scrollToMonth(currentMonth)
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
                    loadDailyGoals(token = token, date = selectedDate)
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

//                val events = eventsForDate.map { goal ->
//                    HomeTimer(goal.goalId, goal.goalName)
//                }
//
//                putParcelableArrayList("events", ArrayList(events))
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

    private fun loadMyGoalList(token: String?) {
        if(token == null) {
            Log.d("HomeFragment", "loadMyGoalList token null")
            return
        }
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getMyGoalList(token = "Bearer $token")
                if (response.isSuccess) {
                    val goals = response.result
                    for (goal in goals) {
                        Log.d("HomeFragmentApi","Goal: ${goal.goalName} / type: ${goal.goalType}")
                        eventList+(CalendarEvent(goal.goalName,"DAY",goal.frequency,LocalDate.now()))
                        //
                    }
                    Log.d("HomeFragmentApi","eventList: $eventList")
                } else {
                    Toast.makeText(requireContext(), "목표 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFriendGoalList(token: String, friendId: Int) {
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getFriendGoalList(token = "Bearer $token", friendId = friendId)
                if (response.isSuccess) {
                    val goals = response.result
                    // goals 리스트를 RecyclerView 등에 표시
                    for (goal in goals) {
                        Log.d("HomeFragmentApi","Goal: ${goal.goalName} / type: ${goal.goalType}")
                        //
                    }
                } else {
                    Toast.makeText(requireContext(), "친구 목표 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFriendsList(token: String?) {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.friendApi
                val response = apiService.getFriendSummary(token = "Bearer $token")
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    friendList = response.body()!!.result.first().friendInfoSummaryList.take(3)
                    // goals 리스트를 RecyclerView 등에 표시
                } else {
                    Toast.makeText(requireContext(), "친구 리스트 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadGoalDetail(token: String, goalId: Int) {
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getGoalDetail(token = "Bearer $token", goalId = goalId)
                if (response.isSuccess) {


                } else {
                    Toast.makeText(requireContext(), "친구 목표 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getMyWeeklyReport(token: String?) {
        lifecycleScope.launch {
            try{
                val response = RetrofitInstance.weeklyReportApi.getWeeklyReports(
                    token = "Bearer $token",
                    year = today.year,
                    month = today.monthValue,
                    week = today.get(WeekFields.of(Locale.KOREA).weekOfYear()),
                    userId = prefs.getInt("userId", 0)
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    val goalReports = body?.result?.goalReports

                    goalReports?.forEach { report ->
                        // 제목 길이 제한
                        val title = if (report.goalTitle.length > 10) {
                            report.goalTitle.take(10) + "..."
                        } else {
                            report.goalTitle
                        }

                        val rate = report.achievementRate
                        Log.d("HomeFragmentApi", "Goal: $title, Achievement: $rate%")
                    }

                } else {
                    Log.d("HomeFragmentApi","getWeeklyReport 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("HomeFragmentApi","네트워크 오류")
            }

        }
    }

    private fun getFriendWeeklyReport(token: String?, friendId: Int) {
        lifecycleScope.launch {
            try{
                val response = RetrofitInstance.weeklyReportApi.getWeeklyReports(
                    token = "Bearer $token",
                    year = today.year,
                    month = today.monthValue,
                    week = today.get(WeekFields.of(Locale.KOREA).weekOfYear()),
                    userId = prefs.getInt("userId", 0)
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    val goalReports = body?.result?.goalReports
                    var rateArray: List<Float> = listOf()
                    goalReports?.forEach { report ->
                        // 제목 길이 제한
                        val title = report.goalTitle
                        val rate = report.achievementRate.toFloat()
                        if(rateArray.size < 3) rateArray+=rate
                        Log.d("HomeFragmentApi", "Goal: $title, Achievement: $rate%")
                    }
                    friendList.forEach { friend ->
                        val item = FriendChallengeItem(
                            friend.nickname,
                            "평균 목표 달성률 : ${rateArray.average().toInt()}%",
                            R.drawable.ic_launcher_background,
                            rateArray
                        )
                        dummyData+=item
                    }
                } else {
                    Log.d("HomeFragmentApi","getWeeklyReport 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("HomeFragmentApi","네트워크 오류")
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

                    // 기존 이벤트 초기화
                    //eventList.clear()

                    // API 응답을 CalendarEvent로 변환
                    dailyGoals.forEach { goal ->
                        eventList.add(
                            CalendarEvent(
                                goalName = goal.goalName,
                                period = goal.period,
                                frequency = goal.frequency,
                                date = date
                            )
                        )
                    }
                } else {
                    Log.d("CalendarActivity", "Error: ${response.message}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("CalendarActivity", "Exception: $e")
            }
        }
    }

}