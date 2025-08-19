package com.example.planup.main.home.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.MainActivity
import com.example.planup.main.home.adapter.CalendarEventAdapter
import com.example.planup.network.RetrofitInstance
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private val today = LocalDate.now()
    private var selectedDate = today
    private lateinit var eventAdapter: CalendarEventAdapter
    private lateinit var prefs: SharedPreferences


    private val eventList = mutableListOf<CalendarEvent>(
        CalendarEvent("토익 공부하기", "DAY", 1, LocalDate.of(2025, 8, 17)),
        CalendarEvent("헬스장 가기", "DAY", 1, LocalDate.of(2025, 8, 18)),
        CalendarEvent("스터디 모임", "DAY", 1, LocalDate.of(2025, 8, 19)),
        CalendarEvent("<인간관계론> 읽기", "DAY", 1, LocalDate.of(2025, 8, 18)),
        CalendarEvent("영어 단어 외우기", "DAY", 1, LocalDate.of(2025, 8, 20)),
        CalendarEvent("코틀린 공부", "DAY", 1, LocalDate.of(2025, 8, 21))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val monthYearText = findViewById<TextView>(R.id.monthYearText)
        val eventsRecyclerView = findViewById<RecyclerView>(R.id.eventsRecyclerView)
        val backBtn = findViewById<ImageView>(R.id.calendar_back_home_iv)
        val arrowNext = findViewById<ImageView>(R.id.calendar_arrow_next_iv)
        val arrowBefore = findViewById<ImageView>(R.id.calendar_arrow_before_iv)
        prefs = getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)

        eventAdapter = CalendarEventAdapter()
        eventsRecyclerView.adapter = eventAdapter
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        // 초기 오늘 날짜 목표 불러오기
        loadDailyGoals(token = token, date = today)

        val daysOfWeek = daysOfWeekFromLocale()
        var currentMonth = YearMonth.now()
        calendarView.setup(currentMonth.minusMonths(12), currentMonth.plusMonths(12), daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        monthYearText.text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

        arrowBefore.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            calendarView.scrollToMonth(currentMonth)
        }

        arrowNext.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            calendarView.scrollToMonth(currentMonth)
        }

        calendarView.monthScrollListener = { month ->
            currentMonth = month.yearMonth
            monthYearText.text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
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
                    updateEventList(date)
                    //loadDailyGoals(token = token, date = selectedDate)
                }
            }
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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

                    // RecyclerView 갱신
                    updateEventList(date)
                } else {
                    Log.d("CalendarActivity", "Error: ${response.message}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("CalendarActivity", "Exception: $e")
            }
        }
    }

    private fun updateEventList(date: LocalDate) {
        val events = getEventsForDate(date).map { it.goalName }
        eventAdapter.submitList(events)
    }

    private fun getEventsForDate(date: LocalDate): List<CalendarEvent> {
        return eventList.filter { it.date == date }
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
        val days = DayOfWeek.values()
        return days.drop(firstDayOfWeek.ordinal) + days.take(firstDayOfWeek.ordinal)
    }
}

// API 기반 CalendarEvent 데이터 클래스
data class CalendarEvent(
    val goalName: String,
    val period: String,
    val frequency: Int,
    val date: LocalDate
)
