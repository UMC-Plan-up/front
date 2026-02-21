package com.example.planup.main.goal.ui

import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentPushAlertBinding
import com.example.planup.databinding.ItemRecyclerDropdownMoriningBinding
import com.example.planup.databinding.ItemRecyclerDropdownTimeBinding
import com.example.planup.goal.adapter.TimerRVAdapter
import com.example.planup.goal.util.goalType
import com.example.planup.goal.util.setInsets
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.viewmodel.GoalViewModel
import com.example.planup.main.home.ui.HomeFragment
import com.example.planup.network.RetrofitInstance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Collections.frequency

@AndroidEntryPoint
class PushAlertEditFragment : Fragment() {
    private var _binding: FragmentPushAlertBinding? = null
    private val binding get() = _binding!!
    private var isFirst = true

    private lateinit var hours: ArrayList<String> //시간
    private lateinit var minutes: ArrayList<String> //분
    private lateinit var times: ArrayList<String>
    private lateinit var prefs: SharedPreferences

    private val viewModel: GoalViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPushAlertBinding.inflate(inflater, container, false)
        viewModel.goalData.apply {
            val request = EditGoalRequest(
                goalName = goalName,
                goalAmount = goalAmount,
                goalCategory = goalCategory,
                goalType = goalType,
                oneDose = oneDose,
                frequency = frequency,
                period = period,
                endDate = endDate,
                verificationType = verificationType,
                limitFriendCount = limitFriendCount,
                goalTime = goalTime
            )
            Log.d("EditGoalFragment", "$request")
        }
        init()
        clickListener()
        binding.alertBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.nextButton.setOnClickListener {
            if (viewModel.editGoalData == null) return@setOnClickListener
            viewModel.goalData.apply {
                val request = EditGoalRequest(
                    goalName = goalName,
                    goalAmount = goalAmount,
                    goalCategory = goalCategory,
                    goalType = goalType,
                    oneDose = oneDose,
                    frequency = frequency,
                    period = period,
                    endDate = endDate,
                    verificationType = verificationType,
                    limitFriendCount = limitFriendCount,
                    goalTime = goalTime
                )
                Log.d("EditGoalFragment", "$request")
                updateGoal(goalId = viewModel.goalId, request = request)
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets(view)
    }

    private fun updateGoal(goalId: Int, request: EditGoalRequest) {
        lifecycleScope.launch {
            try {
//                val token = prefs.getString("accessToken", null)
                val response = RetrofitInstance.goalApi.editGoal(goalId = goalId, request)
                if (response.isSuccess){
                    val nextFragment = EditGoalCompleteFragment()
                    val bundle = Bundle().apply {
                        putString("goalId", goalId.toString())
                    }
                    nextFragment.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.edit_friend_goal_fragment_container, nextFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    val errorMessage = response.message
                    Log.d("EditGoalFragment", "에러 메시지: $errorMessage")
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    Log.e("API", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
                } else {
                    Log.e("API", "Other error: ${e.message}", e)
                }
            }
        }
    }

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
            val layout = layoutInflater.inflate(R.layout.toast_grey_template, null) as android.widget.LinearLayout
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
