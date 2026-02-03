// RecordFragment.kt
package com.example.planup.main.record.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.ui.HomeAlertFragment
import com.example.planup.main.record.adapter.NotificationAdapter
import com.example.planup.main.record.data.BadgeDTO
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import com.example.planup.main.record.ui.viewmodel.RecordViewModel
import com.example.planup.network.ApiResult

@AndroidEntryPoint
class RecordFragment :  Fragment() {

    private lateinit var binding: FragmentRecordBinding
    private val viewModel: RecordViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter

    private val TAG = "RecordFragment"

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)

        viewModel.loadUserInfo()
        setUpDropdownList()
        setupNotificationRecycler()
        fetchWeeklyPageData()      // 주간 페이지(배지/알림/응원) 조회
        applySpannableTitle()
        setClickListeners()

        return binding.root
    }

    /** 드롭 다운 설정 **/
    private fun setUpDropdownList(){
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

    /** 주간 페이지 데이터(응원 문구, 배지, 알림) 조회 */
    private fun fetchWeeklyPageData() {
        viewModel.loadWeeklyGoalReport(createErrorHandler("fetchWeeklyPageData") {
            binding.balloonText.text = it.cheering
            updateBadges(viewModel.badgeList.value.take(3))
            notificationAdapter.submitList(viewModel.notificationList.value)

        })
    }

    /** (선택) 월별 주차 리스트 조회 - 새 API(getMonthlyReports)에 맞춘 유틸 */
    private fun prefetchMonthlyWeeks(year: Int, month: Int) {
        viewModel.loadMonthlyReport(year, month)
    }

    /** 배지 3칸 업데이트 (아이콘/이름) */
    private fun updateBadges(badges: List<BadgeDTO>) {
        fun apply(iv: ImageView?, nameTv: TextView?, b: BadgeDTO?, fallbackName: String, fallbackIcon: Int) {
            nameTv?.text = b?.badgeName ?: fallbackName
            iv?.setImageResource(
                when (b?.badgeType) {
                    "INFLUENTIAL_STARTER"  -> R.drawable.img_badge_leaf
                    "CONVERSATION_STARTER" -> R.drawable.img_badge_trophy
                    "CHALLENGE_STARTER"    -> R.drawable.img_badge_medal
                    else                   -> fallbackIcon
                }
            )
        }

        // 1번째
        binding.root.findViewById<ImageView>(R.id.img_badge)?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            apply(iv, nameTv, badges.getOrNull(0), "영향력 있는 시작", R.drawable.img_badge_leaf)
        }
        // 2번째
        binding.root.findViewById<ImageView>(R.id.img_badge)?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            apply(iv, nameTv, badges.getOrNull(1), "대화의 시작", R.drawable.img_badge_trophy)
        }
        // 3번째
        binding.root.findViewById<ImageView>(R.id.img_badge)?.let { iv ->
            val parent = iv.parent as? LinearLayout
            val nameTv = parent?.getChildAt(1) as? TextView
            apply(iv, nameTv, badges.getOrNull(2), "도전의 시작", R.drawable.img_badge_medal)
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

    /** 선택된 연도/월 파싱 */
    private fun parseSelectedYear(): Int {
        val raw = binding.textSelectedYear.text?.toString().orEmpty()
        return raw.filter { it.isDigit() }.toIntOrNull() ?: java.time.LocalDate.now().year
    }
    private fun parseSelectedMonth(): Int {
        val raw = binding.textSelectedMonth.text?.toString().orEmpty()
        return raw.filter { it.isDigit() }.toIntOrNull() ?: java.time.LocalDate.now().monthValue
    }

    /** 지정한 주차의 리포트 화면으로 이동 */
    private fun openWeeklyReport(week: Int) {
        val year = parseSelectedYear()
        val month = parseSelectedMonth()
        Log.d(TAG, "openWeeklyReport(): year=$year, month=$month, week=$week")

        // (선택) 이동 전에 월별 주차 리스트를 미리 받아 로그로 확인
        prefetchMonthlyWeeks(year, month)

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



    fun <T> createErrorHandler(
        tag: String,
        onSuccess: ((T) -> Unit)? = null): (ApiResult<T>) -> Unit {
        return { result ->
            when (result) {
                is ApiResult.Success -> onSuccess?.invoke(result.data)
                is ApiResult.Error -> Log.d(tag, "Error: ${result.message}")
                is ApiResult.Exception -> Log.d(tag, "Exception: ${result.error}")
                is ApiResult.Fail -> Log.d(tag, "Fail: ${result.message}")
            }
        }
    }
}