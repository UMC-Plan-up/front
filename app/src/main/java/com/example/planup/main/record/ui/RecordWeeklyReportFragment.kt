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
import com.example.planup.main.record.adapter.Badge
import com.example.planup.main.record.adapter.DailyRecord
import com.example.planup.main.record.adapter.GoalReport
import com.example.planup.network.RetrofitInstance
import com.example.planup.network.getRetrofit
import com.example.planup.network.port.ChallengePort
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

data class ChallengeCardData(
    val resultText: String,       // "승리" | "패배" | "무승부"
    val opponentName: String,     // 상대 닉네임
    val penaltyText: String,      // 패널티 설명
    val friend1Name: String,
    val friend1Progress: Int,     // 0..100 (나)
    val friend1ProfileRes: Int?,  // drawable res (없으면 null)
    val friend2Name: String,
    val friend2Progress: Int,     // 0..100 (상대)
    val friend2ProfileRes: Int?   // drawable res (없으면 null)
)

class RecordWeeklyReportFragment : Fragment() {
    lateinit var binding: FragmentRecordWeeklyReportBinding

    // 현재 챌린지 카드 상태 저장
    private var currentChallenge: ChallengeCardData? = null

    // 주차 상태(ISO: 월요일 시작)
    private val weekFields = WeekFields.ISO
    private var currentYear: Int = 0
    private var currentMonth: Int = 0   // 1..12
    private var currentWeekOfMonth: Int = 0  // 1..maxWeeksInMonth

    // 주간 리포트에서 계산한 내 평균 달성률(상대 퍼센트가 없으므로 내 퍼센트 대체로 사용)
    private var myAvgAchievementPercent: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordWeeklyReportBinding.inflate(inflater, container, false)

        val today = LocalDate.now()
        currentYear = arguments?.getInt(ARG_YEAR) ?: today.year
        currentMonth = arguments?.getInt(ARG_MONTH) ?: today.monthValue
        currentWeekOfMonth = arguments?.getInt(ARG_WEEK) ?: today.get(weekFields.weekOfMonth())

        updateTitle()
        loadWeeklyReport(currentYear, currentMonth, currentWeekOfMonth)

        // 좌우 화살표
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

        // 완료 화면으로 데이터 전달
        binding.checkChallengeRecordLl.setOnClickListener {
            val c = currentChallenge
            if (c == null) {
                Toast.makeText(requireContext(), "불러올 챌린지 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val frag = com.example.planup.main.home.ui.ChallengeCompleteFragment.newInstance(
                resultText      = c.resultText,
                opponentName    = c.opponentName,
                penaltyText     = c.penaltyText,
                myPercent       = c.friend1Progress,
                opponentPercent = c.friend2Progress,
                myName          = c.friend1Name,
                myProfileRes    = c.friend1ProfileRes,
                oppProfileRes   = c.friend2ProfileRes,
                myBarName       = c.friend1Name,
                oppBarName      = c.friend2Name
            )
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, frag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        return binding.root
    }

    companion object {
        private const val ARG_YEAR = "year"
        private const val ARG_MONTH = "month"
        private const val ARG_WEEK = "week"

        fun newInstance(year: Int, month: Int, week: Int) = RecordWeeklyReportFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_YEAR, year)
                putInt(ARG_MONTH, month)
                putInt(ARG_WEEK, week)
            }
        }
    }

    /** 1:1 챌린지 카드 UI에 값 세팅 */
    private fun bindChallengeCard(data: ChallengeCardData) = with(binding) {
        tvResultValue.text = data.resultText
        tvOpponentValue.text = data.opponentName
        tvPenaltyValue.text = data.penaltyText

        tvFriend1Name.text = data.friend1Name
        pbFriend1.progress = data.friend1Progress
        data.friend1ProfileRes?.let { ivFriend1.setImageResource(it) }

        tvFriend2Name.text = data.friend2Name
        pbFriend2.progress = data.friend2Progress
        data.friend2ProfileRes?.let { ivFriend2.setImageResource(it) }
    }

    /** 이전 주차로 이동 */
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

    /** 다음 주차로 이동 */
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

    /** 해당 년/월의 주차 수(ISO 주차: 월요일 시작) */
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

    // 인증 헤더
    private fun buildAuthHeader(): String? {
        val prefs = requireContext().getSharedPreferences("userInfo", android.content.Context.MODE_PRIVATE)
        val prefsToken: String? = prefs.getString("accessToken", null)
        val appToken: String? = com.example.planup.network.App.jwt.token
        val raw = when {
            !prefsToken.isNullOrBlank() -> prefsToken
            !appToken.isNullOrBlank() -> appToken
            else -> null
        } ?: return null
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

            // 1) 주간 리포트 (내 목표/일일기록/배지 + 내 평균 달성률 산출)
            runCatching {
                RetrofitInstance.weeklyReportApi.getWeeklyReports(
                    token = tokenHeader, year = year, month = month, week = week, userId = userId
                )
            }.onSuccess { response ->
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "조회 실패(${response.code()})", Toast.LENGTH_SHORT).show()
                    return@onSuccess
                }
                val body = response.body()
                if (body?.isSuccess == true) {
                    val r = body.result
                    val nextMsg = r?.nextGoalMessage ?: ""
                    val goals = r?.goalReports.orEmpty()
                    val daily = r?.dailyRecordList.orEmpty()
                    val badges = r?.badgeList.orEmpty()

                    // 내 평균 달성률(0~100) 계산
                    myAvgAchievementPercent = if (goals.isNotEmpty()) {
                        val avg = goals.mapNotNull { it.achievementRate }.ifEmpty { listOf(0) }.average()
                        avg.toInt().coerceIn(0, 100)
                    } else 0

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

            // 2) 기존 ChallengePort만 사용해 챌린지 상대 닉네임 확보(첫 친구)
            val challengePort = getRetrofit().create(ChallengePort::class.java)
            challengePort.showFriends(userId).enqueue(object :
                retrofit2.Callback<com.example.planup.network.data.ChallengeResponse<List<com.example.planup.network.data.ChallengeFriends>>> {
                override fun onResponse(
                    call: retrofit2.Call<com.example.planup.network.data.ChallengeResponse<List<com.example.planup.network.data.ChallengeFriends>>>,
                    response: retrofit2.Response<com.example.planup.network.data.ChallengeResponse<List<com.example.planup.network.data.ChallengeFriends>>>
                ) {
                    val friends = response.body()?.result.orEmpty()
                    val opponentName = friends.firstOrNull()?.nickname ?: "상대"

                    // ★ 현재 API로 penalty, friendPercent는 알 수 없음 → 임시 값
                    val penaltyText = ""             // TODO: API에서 제공되면 교체
                    val myPercent   = myAvgAchievementPercent
                    val friendPct   = 0              // TODO: API에서 상대 퍼센트 제공 시 교체

                    val resultText = when {
                        myPercent > friendPct -> "승리"
                        myPercent < friendPct -> "패배"
                        else -> "무승부"
                    }

                    val myName = getMyDisplayName()  // 내 닉네임(없으면 "나")
                    val mapped = ChallengeCardData(
                        resultText      = resultText,
                        opponentName    = opponentName,
                        penaltyText     = penaltyText,
                        friend1Name     = myName,
                        friend1Progress = myPercent,
                        friend1ProfileRes = null,
                        friend2Name     = opponentName,
                        friend2Progress = friendPct,
                        friend2ProfileRes = null
                    )
                    currentChallenge = mapped
                    bindChallengeCard(mapped)
                }

                override fun onFailure(
                    call: retrofit2.Call<com.example.planup.network.data.ChallengeResponse<List<com.example.planup.network.data.ChallengeFriends>>>,
                    t: Throwable
                ) {
                    // 친구 조회 실패 시 챌린지 카드 숨기거나 더미 유지
                    currentChallenge = null
                    // 필요하다면 섹션 GONE 처리
                    // binding.challengeCardRoot.visibility = View.GONE
                }
            })
        }
    }

    private fun getMyDisplayName(): String {
        val prefs = requireContext().getSharedPreferences("userInfo", android.content.Context.MODE_PRIVATE)
        // 앱에서 닉네임을 저장했다면 키에 맞게 꺼내세요. 없으면 "나"
        return prefs.getString("nickname", null)?.takeIf { it.isNotBlank() } ?: "나"
    }

    // --- 렌더링 ---

    private fun renderGoalReports(goals: List<GoalReport>) {
        val container = binding.goalReportContainer
        container.removeAllViews()

        if (goals.isEmpty()) {
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

            TextView(requireContext()).apply {
                text = localDate.format(dayFormatter)
                textSize = 14f
            }.also(container::addView)

            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

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
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            decodeBase64DataUri(dr.photoVerified)?.let { iv.setImageBitmap(it) }
                ?: iv.setImageResource(android.R.color.darker_gray)
            photoCard.addView(iv)
            row.addView(photoCard)

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
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
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

    // (필요 시 남겨두는 공용 전환 함수)
    private fun setFragmentClick(view: View, fragment: Fragment) {
        view.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}