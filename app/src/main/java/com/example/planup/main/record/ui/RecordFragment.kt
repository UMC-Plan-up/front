package com.example.planup.main.record.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.record.adapter.BadgeDTO
import com.example.planup.main.record.adapter.NotificationAdapter
import com.example.planup.main.record.adapter.NotificationDTO
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch
import kotlin.collections.forEachIndexed

class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding
    private lateinit var notificationAdapter: NotificationAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)

        // ▼ 드롭다운 설정 (생략: 기존 코드 그대로)
        setupDropdown(
            targetView = binding.textSelectedMonth,
            items = listOf("월", "1","2","3","4","5","6","7","8","9","10","11","12"),
            popupLayoutRes = R.layout.popup_month_dropdown,
            listViewId = R.id.listViewMonths,
            itemLayoutRes = R.layout.item_dropdown_month
        ) { selected -> binding.textSelectedMonth.text = "${selected}월" }

        setupDropdown(
            targetView = binding.textSelectedYear,
            items = listOf("연도", "2025","2024","2023","2022","2021","2020"),
            popupLayoutRes = R.layout.popup_year_dropdown,
            listViewId = R.id.listViewYears,
            itemLayoutRes = R.layout.item_dropdown_year
        ) { selected -> binding.textSelectedYear.text = "${selected}년" }

        spannableText()
        clickListener()

        // RecyclerView 초기화
        setupNotificationRecycler()

        // ★ 추가: 배지/응원 데이터 불러오기
        fetchWeeklyPageData()

        return binding.root
    }

    private fun setupNotificationRecycler() {
        notificationAdapter = NotificationAdapter(
            onItemClick = { item ->
                // TODO: url 라우팅 (필요시)
                Toast.makeText(requireContext(), "알림 클릭: ${item.url}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.notificationRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
            isNestedScrollingEnabled = false
        }
    }


    // ★ 추가: 토큰 헤더 만들기 (userInfo 우선, 없으면 App.jwt.token)
    private fun buildAuthHeader(): String? {
        val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val p = prefs.getString("accessToken", null)
        val a = com.example.planup.network.App.jwt.token
        val raw = when {
            !p.isNullOrBlank() -> p
            !a.isNullOrBlank() -> a
            else -> null
        } ?: return null
        return if (raw.startsWith("Bearer ", true)) raw else "Bearer $raw"
    }

    // 1) fetchWeeklyPageData()에서 알림 호출 추가
    private fun fetchWeeklyPageData() {
        val auth = buildAuthHeader() ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = RetrofitInstance.weeklyReportApi.getWeeklyGoalReportRequest(auth)
                val body = resp.body()
                if (resp.isSuccessful && body?.isSuccess == true) {
                    val result = body.result
                    val cheering = result.cheering?.takeIf { it.isNotBlank() }
                        ?: "이번 주도 화이팅! 꾸준함이 실력을 만듭니다 💪"
                    binding.balloonText.text = cheering

                    updateBadges(result.badgeDTOList.take(3))

                    // ★ 알림 리스트 세팅
                    notificationAdapter.submitList(result.notificationDTOList)
                } else {
                    // 실패 시 필요시 처리
                }
            } catch (_: Exception) { }
        }
    }

    // ★ 추가: 배지 영역 업데이트
    private fun updateBadges(badges: List<BadgeDTO>) {
        // XML에 id가 없어서, 기존 레이아웃 구조를 이용해 접근합니다.
        // 배지 3칸은 “나의 배지 기록” 다음 LinearLayout(수평) 안의 3개 세로 LinearLayout입니다.
        // 각 세로 LinearLayout: [ImageView, TextView] 순서.
        // 첫 아이콘 id 들이 있으니 그것을 기준으로 텍스트뷰 찾아 세팅합니다.

        // 1) 첫 번째 칸: img_badge_leaf 의 부모에서 TextView 가져오기
        val leafIv = binding.root.findViewById<ImageView>(R.id.img_badge)
        leafIv?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            val b0 = badges.getOrNull(0)
            nameTv?.text = b0?.badgeName ?: "영향력 있는 시작"
            // 필요 시 타입별 아이콘 매핑 (기본 그대로 사용)
            iv.setImageResource(
                when (b0?.badgeType) {
                    "INFLUENTIAL_STARTER" -> R.drawable.img_badge_leaf
                    "CONVERSATION_STARTER" -> R.drawable.img_badge_trophy
                    "CHALLENGE_STARTER" -> R.drawable.img_badge_medal
                    else -> R.drawable.img_badge_leaf
                }
            )
        }

        // 2) 두 번째 칸: img_badge_trophy 의 부모에서 TextView
        val trophyIv = binding.root.findViewById<ImageView>(R.id.img_badge)
        trophyIv?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            val b1 = badges.getOrNull(1)
            nameTv?.text = b1?.badgeName ?: "대화의 시작"
            iv.setImageResource(
                when (b1?.badgeType) {
                    "INFLUENTIAL_STARTER" -> R.drawable.img_badge_leaf
                    "CONVERSATION_STARTER" -> R.drawable.img_badge_trophy
                    "CHALLENGE_STARTER" -> R.drawable.img_badge_medal
                    else -> R.drawable.img_badge_trophy
                }
            )
        }

        // 3) 세 번째 칸: img_badge_medal 의 부모에서 TextView
        val medalIv = binding.root.findViewById<ImageView>(R.id.img_badge)
        medalIv?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            val b2 = badges.getOrNull(2)
            nameTv?.text = b2?.badgeName ?: "도전의 시작"
            iv.setImageResource(
                when (b2?.badgeType) {
                    "INFLUENTIAL_STARTER" -> R.drawable.img_badge_leaf
                    "CONVERSATION_STARTER" -> R.drawable.img_badge_trophy
                    "CHALLENGE_STARTER" -> R.drawable.img_badge_medal
                    else -> R.drawable.img_badge_medal
                }
            )
        }
    }

    // ▼ 기존 코드: spannable 타이틀
    private fun spannableText(){
        val text1 = "그린 님을 위한\n플랜업의 "
        val text2 = "AI 응원 메시지"
        binding.textMessage.text = SpannableStringBuilder().apply {
            append(text1)
            append(
                text2,
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue_200)),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun clickListener(){
        setFragmentClick(binding.btnBadgeRecords, RecordBadgesFragment())

        // ★ 선택된 연/월을 읽어 주차별로 이동
        binding.btnWeeklyReport1.setOnClickListener { openWeeklyReport(1) }
        binding.btnWeeklyReport2.setOnClickListener { openWeeklyReport(2) }
        binding.btnWeeklyReport3.setOnClickListener { openWeeklyReport(3) }
        binding.btnWeeklyReport4.setOnClickListener { openWeeklyReport(4) }
        binding.btnWeeklyReport5.setOnClickListener { openWeeklyReport(5) }
    }

    // ★ “2025년”, “5월” 텍스트에서 숫자만 파싱
    private fun parseSelectedYear(): Int {
        val raw = binding.textSelectedYear.text?.toString().orEmpty()
        return raw.filter { it.isDigit() }.toIntOrNull() ?: java.time.LocalDate.now().year
    }

    private fun parseSelectedMonth(): Int {
        val raw = binding.textSelectedMonth.text?.toString().orEmpty()
        // "월"이 앞에 붙어 있는 항목을 쓰셨다면 동일하게 숫자만 추출
        return raw.filter { it.isDigit() }.toIntOrNull()
            ?: java.time.LocalDate.now().monthValue
    }

    // ★ 주차를 받아 해당 프래그먼트로 이동
    private fun openWeeklyReport(week: Int) {
        val year = parseSelectedYear()
        val month = parseSelectedMonth()

        val fragment = RecordWeeklyReportFragment.newInstance(year, month, week)
        (requireActivity() as com.example.planup.main.MainActivity)
            .supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun setupDropdown(
        targetView: TextView,
        items: List<String>,
        popupLayoutRes: Int,
        listViewId: Int,
        itemLayoutRes: Int,
        onItemSelected: (String) -> Unit
    ) {
        val popupView = layoutInflater.inflate(popupLayoutRes, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val listView = popupView.findViewById<ListView>(listViewId)
        val adapter = ArrayAdapter(requireContext(), itemLayoutRes, R.id.item_text, items)
        listView.adapter = adapter

        targetView.setOnClickListener {
            val offsetY = resources.getDimensionPixelSize(R.dimen.dropdown_offset_y)
            popupWindow.showAsDropDown(targetView, 0, offsetY)
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            onItemSelected(items[position]); popupWindow.dismiss()
        }
    }

    private fun setFragmentClick(view: View, fragment: Fragment) {
        view.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}