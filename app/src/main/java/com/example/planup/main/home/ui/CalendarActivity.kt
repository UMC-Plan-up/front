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
import com.example.planup.databinding.ActivityCalendarBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.adapter.CalendarEventAdapter
import com.example.planup.network.RetrofitInstance
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import androidx.activity.viewModels
import com.example.planup.main.home.ui.viewmodel.CalendarViewModel
import com.example.planup.network.ApiResult

@AndroidEntryPoint
class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var eventAdapter: CalendarEventAdapter

    private val today = LocalDate.now()
    private var selectedDate = today

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupCalendar()
        setupObservers()
        setupClicks()

        // 오늘 날짜 기준 데이터 요청
        viewModel.loadMonthlyGoals(today,
            onCallBack = { result ->
                when(result) {
                    is ApiResult.Error -> {
                        Log.d("loadMonthlyGoals", "Error: ${result.message}")
                    }

                    is ApiResult.Exception -> {
                        Log.d("loadMonthlyGoals", "Exception: ${result.error}")
                    }

                    is ApiResult.Fail -> {
                        Log.d("loadMonthlyGoals", "Fail: ${result.message}")
                    }

                    else -> {}
                }
            })
    }

    private fun setupRecyclerView() {
        eventAdapter = CalendarEventAdapter()
        binding.eventsRecyclerView.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(this@CalendarActivity)
        }
    }

    private fun setupCalendar() {
        val calendarView = binding.calendarView
        val daysOfWeek = daysOfWeekFromLocale()
        var currentMonth = YearMonth.now()

        calendarView.setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            daysOfWeek.first()
        )
        calendarView.scrollToMonth(currentMonth)

        binding.monthYearText.text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

        binding.calendarArrowBeforeIv.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            calendarView.scrollToMonth(currentMonth)
        }

        binding.calendarArrowNextIv.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            calendarView.scrollToMonth(currentMonth)
        }

        calendarView.monthScrollListener = { month ->
            currentMonth = month.yearMonth
            binding.monthYearText.text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
            viewModel.loadMonthlyGoals(month.yearMonth.atDay(1),
                onCallBack = { result ->
                    when(result) {
                        is ApiResult.Error -> {
                            Log.d("loadMonthlyGoals", "Error: ${result.message}")
                        }

                        is ApiResult.Exception -> {
                            Log.d("loadMonthlyGoals", "Exception: ${result.error}")
                        }

                        is ApiResult.Fail -> {
                            Log.d("loadMonthlyGoals", "Fail: ${result.message}")
                        }

                        else -> {}
                    }
                })
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val date = data.date
                val isFuture = date.isAfter(today)
                container.textView.text = date.dayOfMonth.toString()

                // 선택 표시
                if (!isFuture && date == selectedDate) {
                    container.textView.setBackgroundResource(R.drawable.bg_calendar_select)
                    container.textView.setTextColor(
                        ContextCompat.getColor(container.textView.context, R.color.white)
                    )
                } else {
                    container.textView.background = null
                    container.textView.setTextColor(
                        ContextCompat.getColor(
                            container.textView.context,
                            if (isFuture) R.color.gray_100 else R.color.black_400
                        )
                    )
                }

                if (isFuture) {
                    container.barsContainer.visibility = View.GONE
                } else {
                val events = viewModel.getEventsForDate(date)
                val bars = listOf(container.bar1, container.bar2, container.bar3)
                container.barsContainer.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
                bars.forEach { it.visibility = View.GONE }
                for (i in 0 until minOf(events.size, 3)) bars[i].visibility = View.VISIBLE

                }

                container.view.setOnClickListener(null)
                if(!isFuture) {
                    container.view.setOnClickListener {
                        selectedDate = date
                        calendarView.notifyCalendarChanged()
                        viewModel.selectDate(date)
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        // 선택된 날짜에 해당하는 이벤트 리스트 관찰
        viewModel.selectedDateEvents.observe(this) { events ->
            eventAdapter.submitList(events)
        }

        // 전체 이벤트 갱신 (날짜 표시용)
        viewModel.allEvents.observe(this) {
            binding.calendarView.notifyCalendarChanged()
        }
    }

    private fun setupClicks() {
        binding.calendarBackHomeIv.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun daysOfWeekFromLocale(): List<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val days = DayOfWeek.values()
        return days.drop(firstDayOfWeek.ordinal) + days.take(firstDayOfWeek.ordinal)
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
        val barsContainer: LinearLayout = view.findViewById(R.id.eventBarsContainer)
        val bar1: View = view.findViewById(R.id.eventBar1)
        val bar2: View = view.findViewById(R.id.eventBar2)
        val bar3: View = view.findViewById(R.id.eventBar3)
    }
}
