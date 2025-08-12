package com.example.planup.main.home.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.ActivityCalendarBinding
import com.example.planup.databinding.ItemCalendarDayBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.adapter.CalendarEventAdapter
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*


class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding

    private val today = LocalDate.now()
    private var selectedDate = today
    private lateinit var eventAdapter: CalendarEventAdapter

    private val eventList = listOf(
        CalendarEvent("토익 공부하기", LocalDate.of(2025, 8, 17), LocalDate.of(2025, 8, 20)),
        CalendarEvent("헬스장 가기", LocalDate.of(2025, 8, 18), LocalDate.of(2025, 8, 18)),
        CalendarEvent("스터디 모임", LocalDate.of(2025, 8, 19), LocalDate.of(2025, 8, 22)),
        CalendarEvent("<인간관계론> 읽기", LocalDate.of(2025, 8, 18), LocalDate.of(2025, 8, 25))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarView = binding.calendarView
        val monthYearText = binding.monthYearText
        val eventsRecyclerView = binding.eventsRecyclerView
        val backBtn = binding.calendarBackHomeIv

        eventAdapter = CalendarEventAdapter()
        eventsRecyclerView.adapter = eventAdapter
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        updateEventList(selectedDate)

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
                val date = data.date
                container.textView.text = date.dayOfMonth.toString()

                // 선택 표시
                container.textView.setBackgroundResource(
                    if (date == selectedDate) R.drawable.bg_calendar_select else 0
                )

                // 기간 내 이벤트 가져오기
                val events = getEventsForDate(date)
                val bars = listOf(container.bar1, container.bar2, container.bar3)
                container.barsContainer.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
                bars.forEach { it.visibility = View.GONE }

                for (i in 0 until minOf(events.size, 3)) {
                    bars[i].visibility = View.VISIBLE
                }

                // 날짜 클릭 시
                container.view.setOnClickListener {
                    selectedDate = date
                    calendarView.notifyCalendarChanged()
                    updateEventList(selectedDate)
                }
            }
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }

    private fun updateEventList(date: LocalDate) {
        val events = getEventsForDate(date).map { it.title }
        eventAdapter.submitList(events)
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
        val days = DayOfWeek.entries
        return days.drop(firstDayOfWeek.ordinal) + days.take(firstDayOfWeek.ordinal)
    }

    private fun getEventsForDate(date: LocalDate): List<CalendarEvent> {
        return eventList.filter { event ->
            !date.isBefore(event.startDate) && !date.isAfter(event.endDate)
        }
    }
}

data class CalendarEvent(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)
