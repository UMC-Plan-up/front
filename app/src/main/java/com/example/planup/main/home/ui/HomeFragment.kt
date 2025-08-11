package com.example.planup.main.home.ui

import android.content.Intent
import android.app.Dialog
import com.example.planup.main.home.adapter.FriendChallengeAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.main.home.item.FriendChallengeItem
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentHomeBinding
import com.example.planup.main.friend.data.FriendInfo
import com.example.planup.main.goal.ui.ChallengeAlertFragment
import com.example.planup.main.home.data.DailyToDo
import com.example.planup.main.home.adapter.DailyToDoAdapter
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.CalendarView
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
import com.example.planup.network.RetrofitInstance
import com.example.planup.main.record.ui.ReceivedChallengeFragment
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var dailyRecyclerVIew: RecyclerView
    private lateinit var dailyAdapter: DailyToDoAdapter
    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendAdapter: FriendChallengeAdapter
    private lateinit var calendarCardView : CardView
    private val today = LocalDate.now()
    private var selectedDate = today
    private val eventMap: Map<LocalDate, List<String>> = mapOf(
        LocalDate.of(2025, 7, 17) to listOf("토익 공부하기", "헬스장 가기", "스터디 모임"),
        LocalDate.of(2025, 7, 18) to listOf("<인간관계론> 읽기")
    )

    private lateinit var friendList : List<FriendInfo>

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

        //loadMyGoalList() //api 불러오기


        dailyRecyclerVIew = view.findViewById(R.id.daily_todo_rv)
        dailyRecyclerVIew.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val dailyToDos = listOf(
            DailyToDo("공부", 75, 5),
            DailyToDo("독서", 100, 5),
            DailyToDo("운동", 50, 3)
        )

        dailyAdapter = DailyToDoAdapter(dailyToDos)
        dailyRecyclerVIew.adapter = dailyAdapter

        val progressBar = view.findViewById<ProgressBar>(R.id.daily_todo_pb)
        progressBar.progress = 75

        friendRecyclerView = view.findViewById(R.id.home_friend_challenge_rv)
        friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyData = listOf(
            FriendChallengeItem("블루", "평균 목표 달성률 : 70%", R.drawable.ic_launcher_background, listOf(30f, 50f, 70f)),
            FriendChallengeItem("블루", "평균 목표 달성률 : 70%", R.drawable.ic_launcher_background, listOf(35f, 45f, 65f))
        )

        friendAdapter = FriendChallengeAdapter(dummyData)
        friendRecyclerView.adapter = friendAdapter

        //---------------------달력---------------------
        val calendarView = view.findViewById<CalendarView>(R.id.home_calendarView)
        val monthYearText = view.findViewById<TextView>(R.id.home_monthYearText)


        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        calendarView.setup(currentMonth.minusMonths(12), currentMonth.plusMonths(12), daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        monthYearText.text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

        calendarView.monthScrollListener = { month ->
            monthYearText.text = month.yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()
                container.textView.setBackgroundResource(
                    if (data.date == selectedDate) R.drawable.bg_calendar_select else 0
                )

                val events = eventMap[data.date] ?: emptyList()
                val bars = listOf(container.bar1, container.bar2, container.bar3)
                container.barsContainer.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
                bars.forEach { it.visibility = View.GONE }

                for (i in 0 until minOf(events.size, 3)) {
                    bars[i].visibility = View.VISIBLE
                }

                container.view.setOnClickListener {
                    selectedDate = data.date
                    calendarView.notifyCalendarChanged()
                }
            }
        }

        calendarCardView = view.findViewById(R.id.home_calendar_cardView)
        calendarCardView.setOnClickListener {
            val intent = Intent(requireContext(), CalendarActivity::class.java)
            startActivity(intent)
        }

        val fab = binding.homeFab
        val fragmentTimer = TimerFragment()
        val bundle = Bundle().apply {
            putString("selectedDate", selectedDate.toString())
        }
        Log.d("selectedDate", "selectedDate: ${selectedDate.toString()}")
        fragmentTimer.arguments = bundle
        fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragmentTimer)
                .addToBackStack(null)  // 뒤로 가기 가능하게 하려면 필요
                .commit()
        }
    }
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
        val barsContainer: LinearLayout = view.findViewById(R.id.eventBarsContainer)
        val bar1: View = view.findViewById(R.id.eventBar1)
        val bar2: View = view.findViewById(R.id.eventBar2)
        val bar3: View = view.findViewById(R.id.eventBar3)
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
        binding.imageView5.setOnClickListener {
            showPopup()
        }
    }
    private fun showPopup(){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_challenge)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.findViewById<TextView>(R.id.popup_challenge_notice_tv).text = getString(R.string.popup_challenge_request,"그린")
        dialog.findViewById<TextView>(R.id.popup_challenge_btn).setOnClickListener{
            dialog.dismiss()
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,ReceivedChallengeFragment())
                .replace(R.id.main_container, ChallengeAlertFragment())
                .commitAllowingStateLoss()
        }
        dialog.show()
    }

    private fun loadMyGoalList(token: String) {
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getMyGoalList(token = token)
                if (response.isSuccess) {
                    val goals = response.result
                    // goals 리스트를 RecyclerView 등에 표시
                    for (goal in goals) {
                        Log.d("HomeFragmentApi","Goal: ${goal.goalName} / type: ${goal.goalType}")
                        //
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

    private fun loadFriendGoalList(token: String, friendId: Int) {
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getFriendGoalList(token = token, friendId = friendId)
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

    private fun loadFriendsList(token: String) {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.friendApi
                val response = apiService.getFriendSummary(token = token)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    friendList = response.body()!!.result.first().friendInfoSummaryList
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
}