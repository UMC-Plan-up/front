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

@RequiresApi(Build.VERSION_CODES.O)
class CalendarActivity : AppCompatActivity() {

    private val today = LocalDate.now()
    private var selectedDate = today
    private lateinit var eventAdapter: CalendarEventAdapter

    private val eventMap: Map<LocalDate, List<String>> = mapOf(
        LocalDate.of(2025, 7, 17) to listOf("토익 공부하기", "헬스장 가기", "스터디 모임"),
        LocalDate.of(2025, 7, 18) to listOf("<인간관계론> 읽기")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val monthYearText = findViewById<TextView>(R.id.monthYearText)
        val eventsRecyclerView = findViewById<RecyclerView>(R.id.eventsRecyclerView)
        val backBtn = findViewById<ImageView>(R.id.calendar_back_home_iv)

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
        eventAdapter.submitList(eventMap[date] ?: emptyList())
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
}
