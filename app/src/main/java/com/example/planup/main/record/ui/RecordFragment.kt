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
import com.example.planup.main.home.ui.AlertReactionFragment
import com.example.planup.main.home.ui.HomeAlertFragment
import com.example.planup.main.record.data.BadgeDTO
import com.example.planup.main.record.adapter.NotificationAdapter
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch

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

        setUpDropdownList() // 드롭 다운 설정
        setupNotificationRecycler() // 알림 RecyclerView 설정
        fetchWeeklyPageData() // 주간 페이지 데이터(응원 문구, 배지, 알림) 조회
        applySpannableTitle() // 상단 타이틀 하이라이트 적용
        setClickListeners() // 클릭 리스너 설정

        return binding.root
    }

    /** 드롭 다운 설정 **/
    private fun setUpDropdownList(){
        // 드롭다운(월)
        setupDropdown(
            targetView = binding.textSelectedMonth,
            items = listOf("월", "1","2","3","4","5","6","7","8","9","10","11","12"),
            popupLayoutRes = R.layout.popup_month_dropdown,
            listViewId = R.id.listViewMonths,
            itemLayoutRes = R.layout.item_dropdown_month
        ) { selected -> binding.textSelectedMonth.text = "${selected}월" }

        // 드롭다운(연)
        setupDropdown(
            targetView = binding.textSelectedYear,
            items = listOf("연도", "2025","2024","2023","2022","2021","2020"),
            popupLayoutRes = R.layout.popup_year_dropdown,
            listViewId = R.id.listViewYears,
            itemLayoutRes = R.layout.item_dropdown_year
        ) { selected -> binding.textSelectedYear.text = "${selected}년" }
    }

    /** 알림 RecyclerView 설정 */
    private fun setupNotificationRecycler() {
        notificationAdapter = NotificationAdapter(
            onItemClick = { item ->
                Toast.makeText(requireContext(), "알림 클릭: ${item.url}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.notificationRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
            isNestedScrollingEnabled = false
        }
    }

    /** 인증 헤더 생성 (userInfo > App.jwt.token 순서) */
    private fun buildAuthHeader(): String? {
        val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val prefToken = prefs.getString("accessToken", null)
        val appToken = com.example.planup.network.App.jwt.token
        val raw = when {
            !prefToken.isNullOrBlank() -> prefToken
            !appToken.isNullOrBlank() -> appToken
            else -> null
        } ?: return null
        return if (raw.startsWith("Bearer ", true)) raw else "Bearer $raw"
    }

    /** 주간 페이지 데이터(응원 문구, 배지, 알림) 조회 */
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
                    notificationAdapter.submitList(result.notificationDTOList)
                }
            } catch (_: Exception) { /* 무시 */ }
            // ★ 서버의 전용 응원 메시지가 있다면 최종 덮어쓰기
            fetchEncourageMessage()
        }
    }

    /** 배지 3칸 업데이트 (아이콘/이름) */
    private fun updateBadges(badges: List<BadgeDTO>) {
        // 첫 번째 칸
        binding.root.findViewById<ImageView>(R.id.img_badge)?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            val b0 = badges.getOrNull(0)
            nameTv?.text = b0?.badgeName ?: "영향력 있는 시작"
            iv.setImageResource(
                when (b0?.badgeType) {
                    "INFLUENTIAL_STARTER"  -> R.drawable.img_badge_leaf
                    "CONVERSATION_STARTER" -> R.drawable.img_badge_trophy
                    "CHALLENGE_STARTER"    -> R.drawable.img_badge_medal
                    else                   -> R.drawable.img_badge_leaf
                }
            )
        }

        // 두 번째 칸
        binding.root.findViewById<ImageView>(R.id.img_badge)?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            val b1 = badges.getOrNull(1)
            nameTv?.text = b1?.badgeName ?: "대화의 시작"
            iv.setImageResource(
                when (b1?.badgeType) {
                    "INFLUENTIAL_STARTER"  -> R.drawable.img_badge_leaf
                    "CONVERSATION_STARTER" -> R.drawable.img_badge_trophy
                    "CHALLENGE_STARTER"    -> R.drawable.img_badge_medal
                    else                   -> R.drawable.img_badge_trophy
                }
            )
        }

        // 세 번째 칸
        binding.root.findViewById<ImageView>(R.id.img_badge)?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            val b2 = badges.getOrNull(2)
            nameTv?.text = b2?.badgeName ?: "도전의 시작"
            iv.setImageResource(
                when (b2?.badgeType) {
                    "INFLUENTIAL_STARTER"  -> R.drawable.img_badge_leaf
                    "CONVERSATION_STARTER" -> R.drawable.img_badge_trophy
                    "CHALLENGE_STARTER"    -> R.drawable.img_badge_medal
                    else                   -> R.drawable.img_badge_medal
                }
            )
        }
    }

    /** 상단 타이틀 하이라이트 적용 */
    private fun applySpannableTitle() {
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

    /** 클릭 리스너 설정 */
    private fun setClickListeners() {
        setFragmentClick(binding.btnBadgeRecords, RecordBadgesFragment())

        binding.btnWeeklyReport1.setOnClickListener { openWeeklyReport(1) }
        binding.btnWeeklyReport2.setOnClickListener { openWeeklyReport(2) }
        binding.btnWeeklyReport3.setOnClickListener { openWeeklyReport(3) }
        binding.btnWeeklyReport4.setOnClickListener { openWeeklyReport(4) }
        binding.btnWeeklyReport5.setOnClickListener { openWeeklyReport(5) }

        setFragmentClick(binding.alarmPageIv, HomeAlertFragment())
    }

    /** 선택된 연도 반환 (기본값: 현재 연도) */
    private fun parseSelectedYear(): Int {
        val raw = binding.textSelectedYear.text?.toString().orEmpty()
        return raw.filter { it.isDigit() }.toIntOrNull() ?: java.time.LocalDate.now().year
    }

    /** 선택된 월 반환 (기본값: 현재 월) */
    private fun parseSelectedMonth(): Int {
        val raw = binding.textSelectedMonth.text?.toString().orEmpty()
        return raw.filter { it.isDigit() }.toIntOrNull() ?: java.time.LocalDate.now().monthValue
    }

    /** 지정한 주차의 리포트 화면으로 이동 */
    private fun openWeeklyReport(week: Int) {
        val year = parseSelectedYear()
        val month = parseSelectedMonth()

        val fragment = RecordWeeklyReportFragment.newInstance(year, month, week)
        (requireActivity() as MainActivity)
            .supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    /** 드롭다운 팝업 생성/바인딩 */
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
            onItemSelected(items[position])
            popupWindow.dismiss()
        }
    }

    /** 뷰 클릭 시 지정 프래그먼트로 전환 */
    private fun setFragmentClick(view: View, fragment: Fragment) {
        view.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    // RecordFragment.kt 내부에 추가
    private fun fetchEncourageMessage() {
        val auth = buildAuthHeader() ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { RetrofitInstance.encourageMessageApi.getEncourageMessage(auth) }
                .onSuccess { resp ->
                    val body = resp.body()
                    if (resp.isSuccessful && body != null && body.message.isNotBlank()) {
                        // 주간 리포트의 말풍선 본문 텍스트뷰
                        binding.balloonText.text = body.message
                    }
                }
                .onFailure { /* 네트워크 에러는 무시(기존 문구 유지) */ }
        }
    }
}