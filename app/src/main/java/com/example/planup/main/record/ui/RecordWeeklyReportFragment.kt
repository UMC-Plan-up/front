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
import com.example.planup.main.record.data.Badge
import com.example.planup.main.record.data.DailyRecord
import com.example.planup.main.record.data.GoalReport
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

// 챌린지 카드 표시용 데이터(ChallengeCompleteFragment로 전달)
data class ChallengeCardData(
    val resultText: String,
    val opponentName: String,
    val penaltyText: String,
    val friend1Name: String,
    val friend1Progress: Int,
    val friend1ProfileRes: Int?,
    val friend2Name: String,
    val friend2Progress: Int,
    val friend2ProfileRes: Int?
)

class RecordWeeklyReportFragment : Fragment() {
    lateinit var binding: FragmentRecordWeeklyReportBinding

    private var currentChallenge: ChallengeCardData? = null

    // 주차 계산용(ISO: 월요일 시작)
    private val weekFields = WeekFields.ISO
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentWeekOfMonth: Int = 0

    // 내 평균 달성률(목표별 달성률 평균)
    private var myAvgAchievementPercent: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRecordWeeklyReportBinding.inflate(inflater, container, false)

        val today = LocalDate.now()
        currentYear = arguments?.getInt(ARG_YEAR) ?: today.year
        currentMonth = arguments?.getInt(ARG_MONTH) ?: today.monthValue
        currentWeekOfMonth = arguments?.getInt(ARG_WEEK) ?: today.get(weekFields.weekOfMonth())

        clickListener()
        updateTitle()
        loadWeeklyReport(currentYear, currentMonth, currentWeekOfMonth)

        // ★ 서버 응원 메시지 최신화
        fetchEncourageMessage()

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

    private fun clickListener(){
        // 이전, 이후 주차 이동
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
        binding.backBtn.setOnClickListener{
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordFragment())
                .commitAllowingStateLoss()
        }
        // "기록 확인하러 가기" → 완료 화면 이동
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
    }

    private fun stripQuotes(name: String?): String {
        return name?.trim('"') ?: ""
    }

    private fun bindChallengeCard(data: ChallengeCardData) = with(binding) {
        tvResultValue.text = data.resultText
        tvOpponentValue.text = stripQuotes(data.opponentName)
        tvPenaltyValue.text = data.penaltyText
        tvFriend1Name.text = stripQuotes(data.friend1Name)
        pbFriend1.progress = data.friend1Progress
        data.friend1ProfileRes?.let { ivFriend1.setImageResource(it) }
        tvFriend2Name.text = stripQuotes(data.friend2Name)
        pbFriend2.progress = data.friend2Progress
        data.friend2ProfileRes?.let { ivFriend2.setImageResource(it) }
    }

    private fun moveToPreviousWeek() {
        if (currentWeekOfMonth > 1) {
            currentWeekOfMonth--
        } else {
            if (currentMonth == 1) { currentYear--; currentMonth = 12 } else currentMonth--
            currentWeekOfMonth = maxWeeksInMonth(currentYear, currentMonth)
        }
    }

    private fun moveToNextWeek() {
        val maxWeeks = maxWeeksInMonth(currentYear, currentMonth)
        if (currentWeekOfMonth < maxWeeks) {
            currentWeekOfMonth++
        } else {
            if (currentMonth == 12) { currentYear++; currentMonth = 1 } else currentMonth++
            currentWeekOfMonth = 1
        }
    }

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

    // SharedPreferences에서 인증 토큰을 꺼내 Authorization 헤더 구성
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

    // ====== API 연동 ======

    // 서버 응답이 비어있을 때 임시로 보여줄 "목표별 기록" 더미 (목록 기반 더미 UI)
    private fun buildDummyGoals(): List<GoalReport> = listOf(
        GoalReport(
            id = -1,
            goalTitle = "토익 공부하기",
            goalCriteria = "매주 5번 이상",
            achievementRate = 85,
            goalType = "PERSONAL",
            isCommunity = false
        ),
        GoalReport(
            id = -2,
            goalTitle = "헬스장 가기",
            goalCriteria = "매일 30분 이상",
            achievementRate = 72,
            goalType = "PERSONAL",
            isCommunity = false
        )
    )

    // ★ Dummy: 친구 챌린지 카드 기본값
    private fun buildDummyChallenge(): ChallengeCardData = ChallengeCardData(
        resultText = "승리",
        opponentName = "홍길동",
        penaltyText = "커피 사기",
        friend1Name = "나",
        friend1Progress = 85,
        friend1ProfileRes = R.drawable.img_friend_profile_sample1,
        friend2Name = "홍길동",
        friend2Progress = 70,
        friend2ProfileRes = R.drawable.img_friend_profile_sample2
    )

    private fun loadWeeklyReport(year: Int, month: Int, week: Int) {
        // CoroutineScope + Retrofit: 메인 스레드에서 안전하게 네트워크 호출 결과 처리
        lifecycleScope.launch {
            val tokenHeader = buildAuthHeader()
            if (tokenHeader.isNullOrBlank()) {
                Toast.makeText(requireContext(), "로그인이 필요합니다. (토큰 없음)", Toast.LENGTH_SHORT).show()
                // ★ Dummy: 로그인 전이라도 화면이 허전하지 않게 기본 더미를 그려줌
                binding.balloonText.text = "아직 데이터가 없습니다. 새로운 목표를 향해 달려가 볼까요?"
                renderGoalReports(buildDummyGoals())
                renderDailyRecords(emptyList()) // 내부에서 더미 UI 그림
                renderBadges(emptyList())       // 내부에서 더미 UI 그림
                setupBarChartWithDummy()        // 막대그래프 더미
                currentChallenge = buildDummyChallenge()
                bindChallengeCard(currentChallenge!!)
                return@launch
            }
            val prefs = requireContext().getSharedPreferences("userInfo", android.content.Context.MODE_PRIVATE)
            val userId = prefs.getInt("userId", -1).takeIf { it > 0 } ?: run {
                Toast.makeText(requireContext(), "유효한 사용자 정보가 없습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show()
                // ★ Dummy: 사용자 정보 없을 때도 더미로 채움
                binding.balloonText.text = "아직 데이터가 없습니다. 새로운 목표를 향해 달려가 볼까요?"
                renderGoalReports(buildDummyGoals())
                renderDailyRecords(emptyList())
                renderBadges(emptyList())
                setupBarChartWithDummy()
                currentChallenge = buildDummyChallenge()
                bindChallengeCard(currentChallenge!!)
                return@launch
            }

            // 1) 주간 리포트(내 목표/일일기록/배지) 조회
            runCatching {
                RetrofitInstance.weeklyReportApi.getWeeklyReports(
                    token = tokenHeader, year = year, month = month, week = week, userId = userId
                )
            }.onSuccess { response ->
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "조회 실패(${response.code()})", Toast.LENGTH_SHORT).show()
                    // ★ Dummy Fallback
                    binding.balloonText.text = "아직 데이터가 없습니다. 새로운 목표를 향해 달려가 볼까요?"
                    renderGoalReports(buildDummyGoals())
                    renderDailyRecords(emptyList())
                    renderBadges(emptyList())
                    setupBarChartWithDummy()
                    return@onSuccess
                }
                val body = response.body()
                if (body?.isSuccess == true) {
                    val r = body.result
                    val nextMsg = r?.nextGoalMessage?.ifBlank {
                        "아직 데이터가 없습니다. 새로운 목표를 향해 달려가 볼까요?"
                    } ?: "아직 데이터가 없습니다. 새로운 목표를 향해 달려가 볼까요?"

                    val rawGoals = r?.goalReports.orEmpty()
                    val goals = if (rawGoals.isEmpty()) buildDummyGoals() else rawGoals

                    val dailyList = r?.dailyRecordList.orEmpty()
                    val badges = r?.badgeList.orEmpty()

                    // 평균 달성률(더미 포함 계산)
                    myAvgAchievementPercent = if (goals.isNotEmpty()) {
                        goals.map { it.achievementRate ?: 0 }.average().toInt().coerceIn(0, 100)
                    } else 0

                    binding.balloonText.text = nextMsg
                    renderGoalReports(goals)
                    if (dailyList.isEmpty()) {
                        renderDailyRecords(emptyList()) // 내부에서 더미 UI 렌더
                        setupBarChartWithDummy()
                    } else {
                        renderDailyRecords(dailyList)
                        setupBarChartWithData(dailyList)
                    }
                    if (badges.isEmpty()) renderBadges(emptyList()) else renderBadges(badges)
                } else {
                    Toast.makeText(requireContext(), body?.message ?: "조회 실패(isSuccess=false)", Toast.LENGTH_SHORT).show()
                    // ★ Dummy Fallback
                    binding.balloonText.text = "아직 데이터가 없습니다. 새로운 목표를 향해 달려가 볼까요?"
                    renderGoalReports(buildDummyGoals())
                    renderDailyRecords(emptyList())
                    renderBadges(emptyList())
                    setupBarChartWithDummy()
                }
            }.onFailure {
                it.printStackTrace()
                Toast.makeText(requireContext(), "네트워크 오류: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                // ★ Dummy Fallback
                binding.balloonText.text = "아직 데이터가 없습니다. 새로운 목표를 향해 달려가 볼까요?"
                renderGoalReports(buildDummyGoals())
                renderDailyRecords(emptyList())
                renderBadges(emptyList())
                setupBarChartWithDummy()
            }

            // 2) ChallengePort 예시 호출 (상대 닉네임만 대략 표시)
            val challengePort = getRetrofit().create(ChallengePort::class.java)
            challengePort.showFriends(userId).enqueue(object :
                retrofit2.Callback<com.example.planup.network.data.ChallengeResponse<List<com.example.planup.network.data.ChallengeFriends>>> {
                override fun onResponse(
                    call: retrofit2.Call<com.example.planup.network.data.ChallengeResponse<List<com.example.planup.network.data.ChallengeFriends>>>,
                    response: retrofit2.Response<com.example.planup.network.data.ChallengeResponse<List<com.example.planup.network.data.ChallengeFriends>>>
                ) {
                    val friends = response.body()?.result.orEmpty()
                    if (friends.isEmpty()) {
                        // ★ Dummy: 친구 없음 → 더미 챌린지
                        currentChallenge = buildDummyChallenge()
                        bindChallengeCard(currentChallenge!!)
                        return
                    }
                    val opponentName = friends.firstOrNull()?.nickname ?: "상대"

                    // 현재 API로 penalty, friendPercent는 없음 → 임시값(더미와 동일 로직)
                    val penaltyText = ""
                    val myPercent   = myAvgAchievementPercent
                    val friendPct   = 0

                    val resultText = when {
                        myPercent > friendPct -> "승리"
                        myPercent < friendPct -> "패배"
                        else -> "무승부"
                    }

                    val myName = getMyDisplayName()
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
                    // ★ Dummy: 실패 시 더미 챌린지
                    currentChallenge = buildDummyChallenge()
                    bindChallengeCard(currentChallenge!!)
                }
            })
        }
    }

    private fun getMyDisplayName(): String {
        val prefs = requireContext().getSharedPreferences("userInfo", android.content.Context.MODE_PRIVATE)
        return prefs.getString("nickname", null)?.takeIf { it.isNotBlank() } ?: "나"
    }

    // ====== 목표 리스트 렌더링 (카드 클릭 시 분기) ======
    private fun renderGoalReports(goals: List<GoalReport>) {
        val container = binding.goalReportContainer
        container.removeAllViews()

        if (goals.isEmpty()) {
            // ★ Dummy: 목표 3개 카드 UI를 직접 그림
            listOf(
                Triple("토익 공부하기", "매주 5번 이상", 85),
                Triple("헬스장 가기", "매일 30분 이상", 72),
            ).forEachIndexed { idx, triple ->
                val (titleStr, criteriaStr, rate) = triple

                val item = layoutInflater.inflate(R.layout.item_goal_report, container, false)
                val cpi = item.findViewById<com.google.android.material.progressindicator.CircularProgressIndicator>(R.id.cpi)
                val tvPercent = item.findViewById<TextView>(R.id.tv_percent)
                val tvTitle = item.findViewById<TextView>(R.id.tv_title)
                val tvCriteria = item.findViewById<TextView>(R.id.tv_criteria)

                cpi.isIndeterminate = false
                cpi.max = 100
                cpi.setProgressCompat(rate, true)
                cpi.setIndicatorColor(Color.parseColor("#FF6F61"))
                cpi.trackColor = Color.parseColor("#FFEFEFEF")
                tvPercent.text = "$rate%"
                tvTitle.text = titleStr
                tvCriteria.text = criteriaStr

                val card = CardView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(73)
                    ).apply { if (idx > 0) topMargin = dp(4) }
                    radius = dp(6).toFloat()
                    cardElevation = 0f
                    isClickable = true
                    isFocusable = true
                    foreground = requireContext().getDrawable(android.R.drawable.list_selector_background)
                    setOnClickListener {
                        Toast.makeText(requireContext(), "더미 데이터입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                card.addView(item)
                container.addView(card)
                container.addView(View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(1)
                    ).apply { topMargin = dp(4) }
                    setBackgroundColor(Color.parseColor("#FFEFEFEF"))
                })
            }
            return
        }

        goals.forEachIndexed { idx, g ->
            val rate = (g.achievementRate ?: 0).coerceIn(0, 100)
            val titleStr = g.goalTitle ?: "무제 목표"
            val criteriaStr = g.goalCriteria ?: "기준 정보 없음"

            val item = layoutInflater.inflate(R.layout.item_goal_report, container, false)

            val cpi = item.findViewById<com.google.android.material.progressindicator.CircularProgressIndicator>(R.id.cpi)
            val tvPercent = item.findViewById<TextView>(R.id.tv_percent)
            val tvTitle = item.findViewById<TextView>(R.id.tv_title)
            val tvCriteria = item.findViewById<TextView>(R.id.tv_criteria)

            cpi.isIndeterminate = false
            cpi.max = 100
            cpi.setProgressCompat(rate, /*animated=*/true)
            cpi.setIndicatorColor(Color.parseColor("#FF6F61"))
            cpi.trackColor = Color.parseColor("#FFEFEFEF")

            tvPercent.text = "$rate%"
            tvTitle.text = titleStr
            tvCriteria.text = criteriaStr

            val card = CardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(73)
                ).apply { if (idx > 0) topMargin = dp(4) }
                radius = dp(6).toFloat()
                cardElevation = 0f
                isClickable = true
                isFocusable = true
                foreground = requireContext().getDrawable(android.R.drawable.list_selector_background)
                setOnClickListener {
                    when {
                        isFriendGoal(g) -> navigate(RecordWithFriendsFragment.newInstance(g.id, titleStr))
                        isCommunityGoal(g) -> navigate(RecordWithCommunityFragment.newInstance(g.id, titleStr))
                        else -> Toast.makeText(requireContext(), "이 목표는 상세 화면이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            card.addView(item)
            container.addView(card)

            container.addView(View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(1)
                ).apply { topMargin = dp(4) }
                setBackgroundColor(Color.parseColor("#FFEFEFEF"))
            })
        }
    }

    // 목표 성격 판별 로직
    private fun isFriendGoal(g: GoalReport): Boolean {
        g.goalType?.trim()?.uppercase(Locale.ROOT)?.let { t ->
            if (t == "FRIEND" || t == "1:1" || t == "DUO") return true
        }
        if (g.isCommunity == false) return true
        return false
    }

    private fun isCommunityGoal(g: GoalReport): Boolean {
        g.goalType?.trim()?.uppercase(Locale.ROOT)?.let { t ->
            if (t == "COMMUNITY" || t == "GROUP") return true
        }
        if (g.isCommunity == true) return true
        return false
    }

    // ====== 일자별 카드 렌더링 ======
    private fun renderDailyRecords(records: List<DailyRecord>) {
        val container = binding.dailyRecordContainer
        container.removeAllViews()

        if (records.isEmpty()) {
            // ★ Dummy: 일자별 기록 3건 UI 생성 (오늘 기준 역순 예시)
            val today = LocalDate.now()
            val dayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREA)

            val dummyTriples = listOf(
                Triple(today.minusDays(2), 60, "영어 단어 외움"),
            )

            dummyTriples.forEach { (date, minutesTotal, memo) ->
                TextView(requireContext()).apply {
                    text = date.format(dayFormatter)
                    textSize = 14f
                }.also(container::addView)

                // 행 컨테이너: 높이 고정(예: 88dp)로 두 카드 비율 안정화
                val row = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(88)   // ← 고정 높이
                    )
                }

// --- 사진 카드 (정사각형) ---
                val photoSize = dp(72) // row 높이(88dp)에서 여백 고려해 적당한 정사각 크기
                val photoLp = LinearLayout.LayoutParams(photoSize, photoSize).apply {
                    setMargins(dp(8), dp(8), dp(8), dp(8))
                }
                val photoCard = androidx.cardview.widget.CardView(requireContext()).apply {
                    layoutParams = photoLp
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
                    // ▼ 임의 이미지(없으면 갤러리 아이콘)
                    setImageResource(android.R.drawable.ic_menu_gallery)
                }
                photoCard.addView(iv)
                row.addView(photoCard)

// --- 시간 카드 (남는 영역 전부 차지) ---
                val timeLp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    setMargins(0, dp(8), dp(8), dp(8))  // 사진 카드와의 간격은 좌측 0, 우측 8
                }
                val timeCard = androidx.cardview.widget.CardView(requireContext()).apply {
                    layoutParams = timeLp
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
                    gravity = Gravity.CENTER_VERTICAL or Gravity.START  // ← 왼쪽 정렬 + 수직 중앙
                    setPadding(dp(16), 0, dp(16), 0)                    // ← 좌우 패딩
                }
                val hours = minutesTotal / 60
                val minutes = minutesTotal % 60
                val timeTv = TextView(requireContext()).apply {
                    text = String.format("%02d:%02d:00", hours, minutes)
                    textSize = 35f
                    setTextColor(Color.parseColor("#333333"))
                }
                timeBox.addView(timeTv)
                timeCard.addView(timeBox)
                row.addView(timeCard)

                container.addView(row)

// 메모 카드 (행과 같은 좌우 여백, 위로 8dp)
                if (memo.isNotBlank()) {
                    val memoCard = androidx.cardview.widget.CardView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, dp(74)
                        ).apply { setMargins(dp(8), dp(8), dp(8), dp(8)) } // ← top 8로 맞춤
                        radius = dp(6).toFloat()
                        cardElevation = dp(4).toFloat()
                        preventCornerOverlap = false
                        useCompatPadding = true
                    }
                    val memoTv = TextView(requireContext()).apply {
                        text = memo
                        textSize = 12f
                        setTextColor(Color.parseColor("#333333"))
                        setPadding(dp(10), 0, dp(10), dp(5))
                        gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    }
                    memoCard.addView(memoTv)
                    container.addView(memoCard)
                }
            }
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

            // 사진 영역
            val photoCard = androidx.cardview.widget.CardView(requireContext()).apply {
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

            // 시간 표시 카드
            val timeCard = androidx.cardview.widget.CardView(requireContext()).apply {
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
                val memoCard = androidx.cardview.widget.CardView(requireContext()).apply {
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

    // ====== 배지 렌더링 ======
    // ====== 배지 렌더링 (이전 방식으로 복구) ======
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
                setImageResource(android.R.drawable.star_big_on) // 실제 리소스로 교체 가능
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

    // ====== MPAndroidChart: 데이터 없을 때 더미 막대 ======
    private fun setupBarChartWithDummy() {
        // 월~일 더미 분(min): 60,30,45,80,20,0,100 + 평균
        val vals = floatArrayOf(60f, 30f, 45f, 80f, 20f, 0f, 100f)
        val entries = mutableListOf<BarEntry>()
        for (i in 0..6) entries.add(BarEntry(i.toFloat(), vals[i]))
        val avg = vals.sum() / 7f
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
                valueFormatter = IndexAxisValueFormatter(listOf("월","화","수","목","금","토","일","평균"))
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

    // ====== MPAndroidChart로 요일별 시간 합계를 막대로 표현 ======
    private fun setupBarChartWithData(records: List<DailyRecord>) {
        // 각 요일(월~일) + 평균(마지막)까지 8개 막대
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
                valueFormatter = IndexAxisValueFormatter(listOf("월","화","수","목","금","토","일","평균"))
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

    // ====== 유틸 ======

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

    private fun navigate(to: Fragment) {
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, to)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    // RecordWeeklyReportFragment.kt 내부에 추가
    private fun fetchEncourageMessage() {
        val tokenHeader = buildAuthHeader() ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { RetrofitInstance.encourageMessageApi.getEncourageMessage(tokenHeader) }
                .onSuccess { resp ->
                    val body = resp.body()
                    if (resp.isSuccessful && body != null && body.message.isNotBlank()) {
                        binding.balloonText.text = body.message
                    }
                }
                .onFailure { /* 네트워크 에러는 무시(기존 문구 유지) */ }
        }
    }
}