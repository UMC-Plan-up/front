package com.example.planup.main.record.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordWeeklyReportBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.ui.ChallengeCompleteFragment
import com.example.planup.main.record.adapter.Badge
import com.example.planup.main.record.adapter.DailyRecord
import com.example.planup.main.record.adapter.GoalReport
import com.example.planup.network.RetrofitInstance
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class RecordWeeklyReportFragment : Fragment() {
    lateinit var binding: FragmentRecordWeeklyReportBinding

    // 주차 상태(ISO: 월요일 시작)
    private val weekFields = WeekFields.ISO
    private var currentYear: Int = 0
    private var currentMonth: Int = 0   // 1..12
    private var currentWeekOfMonth: Int = 0  // 1..maxWeeksInMonth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordWeeklyReportBinding.inflate(inflater, container, false)

        // 초기값: 오늘 날짜 기준
        val today = LocalDate.now()
        currentYear = today.year
        currentMonth = today.monthValue
        currentWeekOfMonth = today.get(weekFields.weekOfMonth())

        // 상단 제목 반영
        updateTitle()

        // 첫 로드 데이터
        loadWeeklyReport(currentYear, currentMonth, currentWeekOfMonth)

        // 화살표 리스너
        binding.backReportIv.setOnClickListener {
            moveToPreviousWeek()
            updateTitle()
            loadWeeklyReport(currentYear, currentMonth, currentWeekOfMonth)
        }
        binding.frontReportIv.setOnClickListener {
            moveToNextWeek()
            updateTitle()
            loadWeeklyReport(currentYear, currentMonth, currentWeekOfMonth)
        }

        setFragmentClick(binding.checkChallengeRecordLl, ChallengeCompleteFragment())


        return binding.root
    }

    /** 이전 주차로 이동: 1주차에서 뒤로 가면 전월 마지막 주차로 */
    private fun moveToPreviousWeek() {
        if (currentWeekOfMonth > 1) {
            currentWeekOfMonth--
        } else {
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
        binding.tvReportTitle.text = "${currentYear}년 ${currentMonth}월 ${currentWeekOfMonth}주차 리포트"
    }

    // RecordWeeklyReportFragment.kt 내부에 추가
    private fun buildAuthHeader(): String? {
        val prefs = requireContext().getSharedPreferences("userInfo", android.content.Context.MODE_PRIVATE)
        val prefsToken: String? = prefs.getString("accessToken", null) // 일반 로그인 저장 토큰(접두사 없음일 가능성 큼)
        val appToken: String? = com.example.planup.network.App.jwt.token // 카카오/일반 모두 여기에 있을 수 있음 (접두사 있을 수도/없을 수도)

        // 우선순위: prefsToken -> appToken
        val raw = when {
            !prefsToken.isNullOrBlank() -> prefsToken
            !appToken.isNullOrBlank() -> appToken
            else -> null
        } ?: return null

        // "Bearer "가 붙어 있으면 그대로 사용, 없으면 붙이기
        return if (raw.startsWith("Bearer ", ignoreCase = true)) raw else "Bearer $raw"
    }

    // --- API 연동 ---
    private fun loadWeeklyReport(year: Int, month: Int, week: Int) {
        lifecycleScope.launch {
            val tokenHeader = buildAuthHeader()
            if (tokenHeader.isNullOrBlank()) {
                Toast.makeText(requireContext(), "로그인이 필요합니다. (토큰 없음)", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val prefs = requireContext().getSharedPreferences("userInfo", android.content.Context.MODE_PRIVATE)
            val userId = prefs.getInt("userId", -1).takeIf { it > 0 } ?: run {
                Toast.makeText(requireContext(), "유효한 사용자 정보가 없습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            runCatching {
                RetrofitInstance.weeklyReportApi.getWeeklyReports(
                    token = tokenHeader,
                    year = year,
                    month = month,
                    week = week,
                    userId = userId
                )
            }.onSuccess { response ->
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "조회 실패(${response.code()})", Toast.LENGTH_SHORT).show()
                    return@onSuccess
                }
                val body = response.body()
                if (body?.isSuccess == true) {
                    val r = body.result
                    // result 자체가 null일 수도 있으니 방어
                    val nextMsg = r?.nextGoalMessage ?: ""
                    val goals = r?.goalReports.orEmpty()
                    val daily = r?.dailyRecordList.orEmpty()
                    val badges = r?.badgeList.orEmpty()

                    binding.balloonText.text = nextMsg
                    renderGoalReports(goals)
                    renderDailyRecords(daily)
                    renderBadges(badges)
                    setupBarChartWithData(daily)
                } else {
                    Toast.makeText(requireContext(), body?.message ?: "조회 실패(isSuccess=false)", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                it.printStackTrace()
                Toast.makeText(requireContext(), "네트워크 오류: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- 렌더링 ---

    private fun renderGoalReports(goals: List<GoalReport>) {
        val container = binding.goalReportContainer   // viewBinding 사용 가정
        container.removeAllViews()

        if (goals.isEmpty()) {
            // 빈 상태 표시 (선택)
            val tv = TextView(requireContext()).apply {
                text = "표시할 목표가 없습니다."
                setTextColor(Color.parseColor("#666666"))
                textSize = 14f
                setPadding(dp(16), dp(12), dp(16), dp(12))
            }
            container.addView(tv)
            return
        }

        goals.forEachIndexed { idx, g ->
            val rate = (g.achievementRate ?: 0).coerceIn(0, 100)
            val titleStr = g.goalTitle ?: "무제 목표"
            val criteriaStr = g.goalCriteria ?: "기준 정보 없음"

            val card = CardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(73)
                ).apply { if (idx > 0) topMargin = dp(4) }
                radius = dp(6).toFloat()
                cardElevation = 0f
            }
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val percent = TextView(requireContext()).apply {
                text = "$rate%"
                textSize = 14f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dp(49), dp(49)).apply {
                    leftMargin = dp(10); gravity = Gravity.CENTER_VERTICAL
                }
            }
            val texts = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                    leftMargin = dp(16)
                    gravity = Gravity.CENTER_VERTICAL
                }
            }
            val title = TextView(requireContext()).apply {
                text = titleStr
                setTextColor(Color.parseColor("#222222"))
                textSize = 16f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            val criteria = TextView(requireContext()).apply {
                text = "달성 기준 : $criteriaStr"
                setTextColor(Color.parseColor("#444444"))
                textSize = 14f
            }
            texts.addView(title)
            texts.addView(criteria)
            row.addView(percent)
            row.addView(texts)
            card.addView(row)
            container.addView(card)

            // divider
            val divider = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(1)
                ).apply { topMargin = dp(4) }
                setBackgroundColor(Color.parseColor("#FFEFEFEF"))
            }
            container.addView(divider)
        }
    }

    private fun renderDailyRecords(records: List<DailyRecord>) {
        val container = binding.dailyRecordContainer
        container.removeAllViews()

        if (records.isEmpty()) {
            val tv = TextView(requireContext()).apply {
                text = "일자별 기록이 없습니다."
                setTextColor(Color.parseColor("#666666"))
                textSize = 14f
                setPadding(dp(16), dp(12), dp(16), dp(12))
            }
            container.addView(tv)
            return
        }

        val dayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREA)

        records.sortedBy { safeParseLocalDate(it.date) }.forEach { dr ->
            val localDate = safeParseLocalDate(dr.date)
            val recMin = dr.recordedTime ?: 0

            // 날짜
            TextView(requireContext()).apply {
                text = localDate.format(dayFormatter)
                textSize = 14f
            }.also(container::addView)

            // 사진/시간 Row
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            // 사진 카드
            val photoCard = CardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(dp(80), dp(80)).apply {
                    setMargins(dp(8), dp(8), dp(8), dp(8))
                }
                radius = dp(6).toFloat()
                cardElevation = dp(4).toFloat()
                preventCornerOverlap = false
                useCompatPadding = true
            }
            val iv = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            decodeBase64DataUri(dr.photoVerified)?.let { iv.setImageBitmap(it) }
                ?: iv.setImageResource(android.R.color.darker_gray)
            photoCard.addView(iv)
            row.addView(photoCard)

            // 기록시간 카드
            val timeCard = CardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, dp(80), 1f).apply {
                    setMargins(dp(8), dp(8), dp(8), dp(8))
                }
                radius = dp(6).toFloat()
                cardElevation = dp(4).toFloat()
                preventCornerOverlap = false
                useCompatPadding = true
            }
            val timeBox = LinearLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
            }
            val hours = recMin / 60
            val minutes = recMin % 60
            val timeTv = TextView(requireContext()).apply {
                text = String.format("%02d:%02d:00", hours, minutes)
                textSize = 35f
                setTextColor(Color.parseColor("#333333"))
            }
            timeBox.addView(timeTv)
            timeCard.addView(timeBox)
            row.addView(timeCard)

            container.addView(row)

            // 메모 카드(있을 때만)
            val memo = dr.simpleMessage.orEmpty()
            if (memo.isNotBlank()) {
                val memoCard = CardView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(74)
                    ).apply { setMargins(dp(8), 0, dp(8), dp(8)) }
                    radius = dp(6).toFloat()
                    cardElevation = dp(4).toFloat()
                    preventCornerOverlap = false
                    useCompatPadding = true
                }
                val memoTv = TextView(requireContext()).apply {
                    text = memo
                    textSize = 12f
                    setTextColor(Color.parseColor("#333333"))
                }
                memoTv.setPadding(dp(10), 0, dp(10), dp(5))
                memoCard.addView(memoTv)
                container.addView(memoCard)
            }
        }
    }

    private fun renderBadges(badges: List<Badge>) {
        val container = binding.badgeContainer
        container.removeAllViews()

        if (badges.isEmpty()) {
            val tv = TextView(requireContext()).apply {
                text = "이번주 배지가 없습니다."
                setTextColor(Color.parseColor("#666666"))
                textSize = 14f
                setPadding(dp(16), dp(12), dp(16), dp(12))
            }
            container.addView(tv)
            return
        }

        badges.forEach { b ->
            val name = b.badgeName.orEmpty()

            val col = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { leftMargin = dp(10) }
            }

            val iv = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(dp(48), dp(48))
                setImageResource(android.R.drawable.star_big_on) // TODO: 실제 아이콘 매핑
            }
            val tv = TextView(requireContext()).apply {
                text = name.ifBlank { "배지" }
                textSize = 11f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                layoutParams = ViewGroup.LayoutParams(dp(60), dp(48))
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }

            col.addView(iv)
            col.addView(tv)
            container.addView(col)
        }
    }

    // --- 차트 세팅(응답 데이터 기반) ---
    private fun setupBarChartWithData(records: List<DailyRecord>) {
        // 월(0)~일(6)
        val sums = FloatArray(7) { 0f }

        records.forEach { dr ->
            val d = safeParseLocalDate(dr.date)
            val idx = when (d.dayOfWeek.value) {
                1 -> 0 // Mon
                2 -> 1
                3 -> 2
                4 -> 3
                5 -> 4
                6 -> 5
                7 -> 6 // Sun
                else -> 0
            }
            val minutes = (dr.recordedTime ?: 0).toFloat()
            sums[idx] += minutes
        }

        val entries = mutableListOf<BarEntry>()
        for (i in 0..6) entries.add(BarEntry(i.toFloat(), sums[i]))
        val avg = if (records.isNotEmpty()) sums.sum() / 7f else 0f
        entries.add(BarEntry(7f, avg))

        val dataSet = BarDataSet(entries, "요일별 기록(분)").apply {
            color = Color.parseColor("#508CFF")
            valueTextColor = Color.TRANSPARENT
        }
        val barData = BarData(dataSet).apply { barWidth = 0.5f }

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            setDrawGridBackground(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(
                    listOf("월","화","수","목","금","토","일","평균")
                )
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.parseColor("#4B4B4B")
                textSize = 12f
            }
            legend.isEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            invalidate()
        }
    }

    // --- 유틸 ---

    private fun safeParseLocalDate(dateStr: String?): LocalDate =
        try { if (dateStr != null) OffsetDateTime.parse(dateStr).toLocalDate() else LocalDate.now() }
        catch (_: Exception) {
            try { LocalDate.parse(dateStr ?: "") } catch (_: Exception) { LocalDate.now() }
        }

    private fun decodeBase64DataUri(dataUri: String?): Bitmap? {
        if (dataUri.isNullOrBlank() || !dataUri.startsWith("data:image")) return null
        val base64 = dataUri.substringAfter("base64,", "")
        if (base64.isBlank()) return null
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun dp(value: Int): Int {
        val density = resources.displayMetrics.density
        return (value * density).toInt()
    }

    // fragment 전환
    private fun setFragmentClick(view: View, fragment: Fragment) {
        view.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}