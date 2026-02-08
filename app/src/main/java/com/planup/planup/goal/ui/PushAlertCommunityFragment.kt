package com.example.planup.goal.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentPushAlertBinding
import com.example.planup.goal.adapter.TimerRVAdapter
import com.example.planup.main.home.ui.HomeFragment
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.marginBottom
import com.example.planup.goal.GoalActivity
import com.example.planup.databinding.ItemRecyclerDropdownMoriningBinding
import com.example.planup.databinding.ItemRecyclerDropdownTimeBinding
import com.example.planup.goal.util.backStackTrueGoalNav
import com.example.planup.goal.util.logGoalActivityData
import com.example.planup.goal.util.setInsets

class PushAlertCommunityFragment : Fragment() {

    private var _binding: FragmentPushAlertBinding? = null
    private val binding get() = _binding!!
    private var isFirst = true

    private lateinit var hours: ArrayList<String> //시간
    private lateinit var minutes: ArrayList<String> //분
    private lateinit var times: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPushAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        clickListener()

        logGoalActivityData()

        setInsets(binding.root)
    }

    //프레그먼트 초기화
    private fun init() {
        hours = resources.getStringArray(R.array.dropdown_hour).toCollection(ArrayList())
        minutes = resources.getStringArray(R.array.dropdown_minute_second).toCollection(ArrayList())
        times = resources.getStringArray(R.array.dropdown_morning_afternoon).toCollection(ArrayList())

        // 알림 시간의 기본값을 '오전 7시 30분'으로 설정
        binding.alertTimeTv.text = "오전"
        binding.alertHourTv.text = "07시"
        binding.alertMinuteTv.text = "30분"

        val prefs = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        isFirst = prefs.getBoolean("show_push_alert_popup", true)

        if (isFirst) showPopup()
    }


    private fun clickListener() {
        //뒤로가기 버튼 클릭
        binding.alertBackIv.setOnClickListener {
//            val participantLimitFragment = ParticipantLimitFragment()
//            (requireActivity() as GoalActivity).navigateToFragment(participantLimitFragment)
            parentFragmentManager.popBackStack()
        }

        //알림받기 토글 끄기
        binding.alertReceiveOnIv.setOnClickListener {
            binding.alertReceiveOnIv.visibility = View.GONE
            binding.alertReceiveOffIv.visibility = View.VISIBLE
        }
        //알림받기 토글 켜기
        binding.alertReceiveOffIv.setOnClickListener {
            binding.alertReceiveOnIv.visibility = View.VISIBLE
            binding.alertReceiveOffIv.visibility = View.GONE
        }

        //정기알림 토글 끄기
        binding.alertRegularOnIv.setOnClickListener {
            binding.alertRegularOnIv.visibility = View.GONE
            binding.alertRegularOffIv.visibility = View.VISIBLE
        }
        //정기알림 토글 켜기
        binding.alertRegularOffIv.setOnClickListener {
            binding.alertRegularOnIv.visibility = View.VISIBLE
            binding.alertRegularOffIv.visibility = View.GONE
        }

        //저장 버튼 클릭 (GoalActivity를 통해서만 이동)
        binding.nextButton.setOnClickListener {

            val sharedPreferences = requireActivity().getSharedPreferences("alert_settings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val isAlertsEnabled = binding.alertReceiveOnIv.visibility == View.VISIBLE
            editor.putBoolean("receive_alerts", isAlertsEnabled)
            editor.putBoolean("regular_alerts", binding.alertRegularOnIv.visibility == View.VISIBLE)
            editor.putString("alert_time_of_day", binding.alertTimeTv.text.toString())
            editor.putString("alert_hour", binding.alertHourTv.text.toString())
            editor.putString("alert_minute", binding.alertMinuteTv.text.toString())

            val selectedDays = mutableSetOf<String>()
            val dayViews = listOf(
                binding.alertEverydayTv, binding.alertMondayTv, binding.alertTuesdayTv,
                binding.alertWednesdayTv, binding.alertThursdayTv, binding.alertFridayTv,
                binding.alertSaturdayTv, binding.alertSundayTv
            )

            val dayNames = listOf(
                "everyday", "monday", "tuesday", "wednesday",
                "thursday", "friday", "saturday", "sunday"
            )
            dayViews.forEachIndexed { index, view ->
                if (view.isSelected) {
                    selectedDays.add(dayNames[index])
                }
            }

            editor.putStringSet("alert_days", selectedDays)
            editor.apply()


            // 알림 설정 완료 후 팝업을 다시 보지 않도록 상태 변경
            val appPrefs = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            appPrefs.edit().putBoolean("show_push_alert_popup", false).apply()


            // GoalActivity에 알림 설정 정보 저장
            val activity = requireActivity() as GoalActivity
            activity.apply {
                notificationEnabled = isAlertsEnabled
                regularAlertEnabled = binding.alertRegularOnIv.visibility == View.VISIBLE
                alertTimeOfDay = binding.alertTimeTv.text.toString()
                alertHour = binding.alertHourTv.text.toString()
                alertMinute = binding.alertMinuteTv.text.toString()
                alertDays = selectedDays
            }

            val goalCompleteFragment = GoalCompleteFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", activity.goalOwnerName)
                    putString("goalType", activity.goalType)
                    putString("goalCategory", activity.goalCategory)
                    putString("goalName", activity.goalName)
                    putString("goalAmount", activity.goalAmount)
                    putString("verificationType", activity.verificationType)
                    putString("period", activity.period)
                    putInt("frequency", activity.frequency)
                    putInt("limitFriendCount", activity.limitFriendCount)
                    putInt("goalTime", activity.goalTime)

                    // 알림 설정 정보도 함께 전달
                    putBoolean("notificationEnabled", activity.notificationEnabled)
                    putBoolean("regularAlertEnabled", activity.regularAlertEnabled)
                    putString("alertTimeOfDay", activity.alertTimeOfDay)
                    putString("alertHour", activity.alertHour)
                    putString("alertMinute", activity.alertMinute)
                    putStringArrayList("alertDays", ArrayList(selectedDays))
                }
            }

            backStackTrueGoalNav(goalCompleteFragment,"PushAlertCommunityFragment")
//            activity.navigateToFragment(goalCompleteFragment)

//            showSizedToast(binding.nextButton, getString(R.string.toast_alert_setting))

        }

        //정기 알림 시간 설정
        //오전/오후
        binding.alertTimeTv.setOnClickListener {
            showDropDown(times, R.layout.item_recycler_dropdown_morining, binding.alertTimeTv)
        }
        //시각 설정
        binding.alertHourTv.setOnClickListener {
            showDropDown(hours, R.layout.item_recycler_dropdown_time, binding.alertHourTv)
        }
        //분 설정
        binding.alertMinuteTv.setOnClickListener {
            showDropDown(minutes, R.layout.item_recycler_dropdown_time, binding.alertMinuteTv)
        }

        // 요일 선택 로직
        val selected = ContextCompat.getColor(requireContext(), R.color.blue_200)
        val unselected = ContextCompat.getColor(requireContext(), R.color.black_300)
        val dayViews = listOf(
            binding.alertEverydayTv, binding.alertMondayTv, binding.alertTuesdayTv,
            binding.alertWednesdayTv, binding.alertThursdayTv, binding.alertFridayTv,
            binding.alertSaturdayTv, binding.alertSundayTv
        )

        dayViews.forEach { dayView ->
            dayView.setOnClickListener {
                val isEverydayClicked = dayView.id == R.id.alert_everyday_tv

                if (isEverydayClicked) {
                    // '매일' 버튼 클릭 시
                    dayView.isSelected = !dayView.isSelected
                    val isSelected = dayView.isSelected

                    dayViews.forEach { otherDayView ->
                        otherDayView.isSelected = isSelected
                        otherDayView.setTextColor(if (isSelected) selected else unselected)
                    }
                } else {
                    // 월~일 중 하나를 클릭 시
                    dayView.isSelected = !dayView.isSelected
                    dayView.setTextColor(
                        if (dayView.isSelected) selected else unselected
                    )

                    if (!dayView.isSelected) {
                        binding.alertEverydayTv.isSelected = false
                        binding.alertEverydayTv.setTextColor(unselected)
                    }

                    val allDaysSelected = listOf(
                        binding.alertMondayTv, binding.alertTuesdayTv, binding.alertWednesdayTv,
                        binding.alertThursdayTv, binding.alertFridayTv, binding.alertSaturdayTv,
                        binding.alertSundayTv
                    ).all { it.isSelected }

                    if (allDaysSelected) {
                        binding.alertEverydayTv.isSelected = true
                        binding.alertEverydayTv.setTextColor(selected)
                    }
                }
            }
        }
    }

    //첫 방문인 경우 알림설정 관련 팝업 출력
    private fun showPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.popup_push_alert_setting)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(resources.getColor(R.color.transparent).toDrawable())
        }

        // '아니오' 버튼 클릭
        dialog.findViewById<TextView>(R.id.noButton).setOnClickListener {
            val prefs = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("show_push_alert_popup", false).apply()
            prefs.edit().putBoolean("receive_alerts", false).apply()

            dialog.dismiss()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, HomeFragment())
                .commitAllowingStateLoss()
        }

        // '네' 버튼 클릭
        dialog.findViewById<TextView>(R.id.yesButton).setOnClickListener {
            val appPrefs = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            appPrefs.edit().putBoolean("show_push_alert_popup", false).apply()
            appPrefs.edit().putBoolean("receive_alerts", true).apply()

            binding.alertReceiveOnIv.visibility = View.VISIBLE
            binding.alertReceiveOffIv.visibility = View.GONE

            dialog.dismiss()
        }

        //외부 터치 시 팝업 종료
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    //알림설정 시간 드롭다운
    private fun showDropDown(items: ArrayList<String>, layout: Int, view: TextView) {
        val inflater = LayoutInflater.from(requireContext())

        val popupView = when (layout) {
            R.layout.item_recycler_dropdown_morining -> ItemRecyclerDropdownMoriningBinding.inflate(inflater).root
            R.layout.item_recycler_dropdown_time -> ItemRecyclerDropdownTimeBinding.inflate(inflater).root
            else -> throw IllegalArgumentException("Invalid layout ID")
        }

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setBackgroundDrawable(ContextCompat.getColor(requireContext(), R.color.transparent).toDrawable())
        popupWindow.isOutsideTouchable = true

        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        popupWindow.showAsDropDown(view)

        val adapter = TimerRVAdapter(items)
        adapter.setDropdownListener(object : TimerRVAdapter.DropdownListener {
            override fun setTime(position: Int) {
                val selectedItem = items[position]
                when (view.id) {
                    R.id.alert_hour_tv -> {
                        view.text = "${selectedItem}시"
                    }
                    R.id.alert_minute_tv -> {
                        view.text = "${selectedItem}분"
                    }
                    else -> {
                        view.text = selectedItem
                    }
                }
                popupWindow.dismiss()
            }
        })
        popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).adapter = adapter
    }


    private fun showSizedToast(matchTo: View, text: CharSequence) {
        val doShow: () -> Unit = {
            val layout = layoutInflater.inflate(R.layout.toast_grey_template, null)
            val tv = layout.findViewById<TextView>(R.id.toast_grey_template_tv)

            val lp = matchTo.layoutParams as ViewGroup.MarginLayoutParams
            val w = matchTo.width
            val h = matchTo.height

            layout.setPadding(lp.marginStart, 0, lp.marginEnd, 0)

            tv.layoutParams = android.widget.LinearLayout.LayoutParams(w, h)
            tv.text = text

            Toast(requireContext()).apply {
                view = layout
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 400)
            }.show()
        }

        if (matchTo.width == 0 || matchTo.height == 0) {
            matchTo.post { doShow() }
        } else {
            doShow()
        }
    }
    private fun Int.dp(): Int =
        (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
