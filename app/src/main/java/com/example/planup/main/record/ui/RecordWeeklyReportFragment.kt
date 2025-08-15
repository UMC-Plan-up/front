package com.example.planup.main.record.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentRecordWeeklyReportBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

class RecordWeeklyReportFragment : Fragment() {
    lateinit var binding: FragmentRecordWeeklyReportBinding

    // 주차 상태(ISO: 월요일 시작)
    private val weekFields = WeekFields.ISO  // 또는 WeekFields.of(Locale.KOREA)
    private var currentYear: Int = 0
    private var currentMonth: Int = 0  // 1..12
    private var currentWeekOfMonth: Int = 0  // 1..maxWeeksInMonth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordWeeklyReportBinding.inflate(inflater, container, false)

        val barChart = binding.barChart


        // 초기값: 오늘 날짜 기준
        val today = LocalDate.now()
        currentYear = today.year
        currentMonth = today.monthValue
        currentWeekOfMonth = today.get(weekFields.weekOfMonth())

        // 상단 제목 반영
        updateTitle()

        // 차트 세팅(예시 더미 데이터)
        setupBarChart()

        // 화살표 리스너
        binding.backReportIv.setOnClickListener {
            moveToPreviousWeek()
            updateTitle()
            // TODO: 여기서 서버 호출/데이터 갱신 로직 실행
            // loadWeeklyReport(currentYear, currentMonth, currentWeekOfMonth)
        }

        binding.frontReportIv.setOnClickListener {
            moveToNextWeek()
            updateTitle()
            // TODO: 여기서 서버 호출/데이터 갱신 로직 실행
            // loadWeeklyReport(currentYear, currentMonth, currentWeekOfMonth)
        }

        return binding.root
    }

    /** 이전 주차로 이동: 1주차에서 뒤로 가면 전월 마지막 주차로 */
    private fun moveToPreviousWeek() {
        if (currentWeekOfMonth > 1) {
            currentWeekOfMonth--
        } else {
            // 전월로 이동
            if (currentMonth == 1) {
                currentYear--
                currentMonth = 12
            } else {
                currentMonth--
            }
            currentWeekOfMonth = maxWeeksInMonth(currentYear, currentMonth)
        }
    }

    /** 다음 주차로 이동: 마지막 주차에서 앞으로 가면 다음달 1주차로 */
    private fun moveToNextWeek() {
        val maxWeeks = maxWeeksInMonth(currentYear, currentMonth)
        if (currentWeekOfMonth < maxWeeks) {
            currentWeekOfMonth++
        } else {
            // 다음달로 이동
            if (currentMonth == 12) {
                currentYear++
                currentMonth = 1
            } else {
                currentMonth++
            }
            currentWeekOfMonth = 1
        }
    }

    /** 해당 년/월의 주차 수(ISO 주차: 월요일 시작, 그 달에 걸친 모든 주차를 카운트) */
    private fun maxWeeksInMonth(year: Int, month: Int): Int {
        val ym = YearMonth.of(year, month)
        var max = 0
        for (d in 1..ym.lengthOfMonth()) {
            val week = LocalDate.of(year, month, d).get(weekFields.weekOfMonth())
            if (week > max) max = week
        }
        return max.coerceAtLeast(1)
    }

    private fun updateTitle() {
        binding.tvReportTitle.text =
            "${currentYear}년 ${currentMonth}월 ${currentWeekOfMonth}주차 리포트"
    }

    private fun setupBarChart() {
        val barChart = binding.barChart

        val entries = listOf(
            BarEntry(0f, 2f),
            BarEntry(1f, 6f),
            BarEntry(2f, 5f),
            BarEntry(3f, 7f),
            BarEntry(4f, 1f),
            BarEntry(5f, 4f),
            BarEntry(6f, 5f),
            BarEntry(7f, 5f) // 평균
        )

        val dataSet = BarDataSet(entries, "요일별 기록").apply {
            color = Color.parseColor("#508CFF")
            valueTextColor = Color.TRANSPARENT
        }

        val barData = BarData(dataSet).apply { barWidth = 0.5f }

        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(
                listOf("월", "화", "수", "목", "금", "토", "일", "평균")
            )
            setDrawGridLines(false)
            granularity = 1f
            textColor = Color.parseColor("#4B4B4B")
            textSize = 12f
        }
        barChart.legend.isEnabled = false
        barChart.setScaleEnabled(false)
        barChart.setPinchZoom(false)
        barChart.invalidate()
    }



}