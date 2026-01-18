package com.example.planup.main.home.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentHomeBinding
import com.example.planup.databinding.ItemCalendarDayBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.adapter.DailyToDoAdapter
import com.example.planup.main.home.adapter.FriendChallengeAdapter
import com.example.planup.main.home.data.CalendarEvent
import com.example.planup.main.home.data.ChallengeReceivedTimer
import com.example.planup.main.home.ui.viewmodel.HomeViewModel
import com.example.planup.network.ApiResult
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences

    private lateinit var dailyAdapter: DailyToDoAdapter
    private lateinit var friendChallengeAdapter: FriendChallengeAdapter

    private val today = LocalDate.now()
    private var selectedDate = today
    private var eventList = mutableListOf<CalendarEvent>(
        CalendarEvent("토익 공부하기", "DAY", 1, LocalDate.of(2025, 12, 11)),
        CalendarEvent("헬스장 가기", "DAY", 1, LocalDate.of(2025, 12, 18)),
        CalendarEvent("스터디 모임", "DAY", 1, LocalDate.of(2025, 12, 19)),
        CalendarEvent("<인간관계론> 읽기", "DAY", 1, LocalDate.of(2025, 12, 18))
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val nickname = prefs.getString("nickname","")
        binding.homeMainTv.text = getString(R.string.home_main_text,nickname?.removeSurrounding("\""))

        setupAdapters()
        observeViewModel()
        setupCalendar()
        setupClickListeners()

        // load data
        viewModel.loadMyGoalList(createErrorHandler("loadMyGoalList"))
        viewModel.loadFriendChallenges(createErrorHandler("loadFriendChallenges"))
        viewModel.loadMonthEvents(YearMonth.now())
    }

    private fun setupAdapters() {
        dailyAdapter = DailyToDoAdapter()
        binding.dailyTodoRv.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        friendChallengeAdapter = FriendChallengeAdapter() { item ->
            // 클릭 처리
            val fragment = FriendGoalListFragment.newInstance(
                item.friendId, item.name, item.profileResId.toString()
            )
            // 예시
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_container,
                    fragment
                ) // fragment_container는 activity/fragment에 맞게 수정
                .addToBackStack(null)
                .commit()
        }
        binding.homeFriendChallengeRv.apply {
            adapter = friendChallengeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyToDos.collect { list ->
//                dailyAdapter = DailyToDoAdapter(list.toMutableList())
//                dailyRecyclerVIew.adapter = dailyAdapter
                    dailyAdapter.submitList(list)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.friendChallenges.collect { list ->
//                    friendAdapter = FriendChallengeAdapter(list.take(3)) { item -> /* 클릭 처리 */ }
//                    friendRecyclerView.adapter = friendAdapter
                    friendChallengeAdapter.submitList(list.take(3))
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.calendarEvents.collect { _ ->
//                    eventList = viewModel.calendarEvents.value.toMutableList()
                    binding.homeCalendarView.notifyCalendarChanged()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.homeFab.setOnClickListener {
            val fragment = TimerFragment().apply {
                arguments = Bundle().apply { putString("selectedDate", selectedDate.toString()) }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.homeMainProfileIv.setOnClickListener {
            showPopup()
        }

        val calendarCardView = binding.homeCalendarCardView
        calendarCardView.setOnClickListener {
            val intent = Intent(requireContext(), CalendarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupCalendar() {
        val calendarView = binding.homeCalendarView
        val monthYearText = binding.homeMonthYearText
        val prevArrow = binding.homeCalendarArrowPrevIv
        val nextArrow = binding.homeCalendarArrowNextIv

        val daysOfWeek = daysOfWeekFromLocale()
        var currentMonth = YearMonth.now()

        // CalendarView 초기화
        calendarView.setup(currentMonth.minusMonths(12), currentMonth.plusMonths(12), daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)
        monthYearText.text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

        // 달 변경 시 텍스트 업데이트
        calendarView.monthScrollListener = { month ->
            monthYearText.text = month.yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        }

        // 이전/다음 화살표 클릭
        prevArrow.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            calendarView.scrollToMonth(currentMonth)
            viewModel.loadMonthEvents(currentMonth)
        }

        nextArrow.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            calendarView.scrollToMonth(currentMonth)
            viewModel.loadMonthEvents(currentMonth)
        }

        // dayBinder 설정
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: com.kizitonwose.calendar.core.CalendarDay) {
                val date = data.date
                container.textView.text = date.dayOfMonth.toString()

                // 선택 표시
                container.textView.setBackgroundResource(
                    if (date == selectedDate) R.drawable.bg_calendar_select else 0
                )
                container.textView.setTextColor(
                    if (date == selectedDate)
                        ContextCompat.getColor(container.textView.context, R.color.white)
                    else
                        ContextCompat.getColor(container.textView.context, R.color.black_400)
                )

                // 이벤트 가져오기 (ViewModel observe에서 업데이트된 값 사용)
                val events = viewModel.calendarEvents.value.filter { it.date == date }
                val bars = listOf(container.bar1, container.bar2, container.bar3)
                container.barsContainer.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
                bars.forEach { it.visibility = View.GONE }
                for (i in 0 until minOf(events.size, 3)) {
                    bars[i].visibility = View.VISIBLE
                }

                // 날짜 클릭
                container.view.setOnClickListener {
                    selectedDate = date
                    calendarView.notifyCalendarChanged()

                    // 선택한 날짜 API 호출
                    viewModel.loadDailyGoals(selectedDate, createErrorHandler("loadDailyGoals"))
                }
            }
        }
    }
    fun daysOfWeekFromLocale(): List<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val days = DayOfWeek.values().toList()
        return days.drop(firstDayOfWeek.ordinal) + days.take(firstDayOfWeek.ordinal)
    }
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val binding = ItemCalendarDayBinding.bind(view)
        val textView: TextView = binding.calendarDayText
        val barsContainer: LinearLayout = binding.eventBarsContainer
        val bar1: View = binding.eventBar1
        val bar2: View = binding.eventBar2
        val bar3: View = binding.eventBar3
    }

    fun <T> createErrorHandler(tag: String): (ApiResult<T>) -> Unit {
        return { result ->
            when (result) {
                is ApiResult.Error -> Log.d(tag, "Error: ${result.message}")
                is ApiResult.Exception -> Log.d(tag, "Exception: ${result.error}")
                is ApiResult.Fail -> Log.d(tag, "Fail: ${result.message}")
                else -> {}
            }
        }
    }

    private fun getEventsForDate(date: LocalDate): List<CalendarEvent> {
        return eventList.filter { it.date == date }
    }
    private fun showPopup(){
        val timerChallenge = ChallengeReceivedTimer(
            1,
            16,
            listOf(13L),
            "friend",
            "goalName",
            "goalAmount",
            1200,
            "endDate",
            "duration",
            "frequency",
            "penalty"
        )
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_challenge)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            //팝업에 요청한 친구 이름 바인딩
            dialog.findViewById<TextView>(R.id.popup_challenge_notice_tv).text = getString(R.string.popup_challenge_request,"그린")
            //배경 투명색
            dialog.setCanceledOnTouchOutside(true)
        }
        //닫기 버튼 클릭 시 팝업 종료
        dialog.findViewById<View>(R.id.popup_challenge_close_iv).setOnClickListener {
            dialog.dismiss()
        }
        //확인하러 가기 버튼 클릭 시 챌린지 요청 조회 화면으로 이동
        dialog.findViewById<TextView>(R.id.popup_challenge_btn).setOnClickListener{
            dialog.dismiss()

            //API response에 따라 photo 또는 timer로 전환
            //bundle로 데이터 넘기는 작업도 필요함
            val challengeTimer = ChallengeReceivedTimerFragment()
            challengeTimer.arguments = Bundle().apply {
                putParcelable("receivedChallenge",timerChallenge)
            }
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,challengeTimer)
                .commitAllowingStateLoss()
        }
        dialog.show()
    }
    }

