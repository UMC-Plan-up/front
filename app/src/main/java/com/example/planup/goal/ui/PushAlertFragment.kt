package com.example.planup.goal.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetAlertBinding
import com.example.planup.goal.adapter.TimerRVAdapter
import com.example.planup.main.MainActivity
import androidx.core.graphics.drawable.toDrawable
import com.example.planup.goal.GoalActivity
import com.example.planup.databinding.ToastGreyTemplateBinding
import com.example.planup.databinding.PopupPushAlertSettingBinding
import com.example.planup.databinding.ItemRecyclerDropdownMoriningBinding
import com.example.planup.databinding.ItemRecyclerDropdownTimeBinding
import android.content.Context

class PushAlertFragment : Fragment() {
    lateinit var binding: FragmentChallengeSetAlertBinding
    private var isFirst = true //해당 페이지 처음 들어온 경우 알림설정에 대한 팝업을 보여줘야 함

    private lateinit var hours: ArrayList<String> //시간
    private lateinit var minutes: ArrayList<String> //분
    private lateinit var times: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetAlertBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    //프레그먼트 초기화
    private fun init(){
        hours = resources.getStringArray(R.array.dropdown_hour).toCollection(ArrayList<String>())
        minutes = resources.getStringArray(R.array.dropdown_minute_second).toCollection(ArrayList<String>())
        times = resources.getStringArray((R.array.dropdown_morning_afternoon)).toCollection(ArrayList<String>())

        applySavedSettings() // 저장된 설정 불러오기

        if (isFirst) showPopup()
    }
    //클릭 이벤트
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
        //저장 버튼 클릭
        binding.alertSaveBtn.setOnClickListener {
            saveAlertSettings() // 저장 함수 호출
            makeToast()

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        //정기 알림 시간 설정
        //오전/오후
        binding.alertTimeTv.setOnClickListener {
            showDropDown(times,R.layout.item_recycler_dropdown_morining,binding.alertTimeTv)
        }
        //시각 설정
        binding.alertHourTv.setOnClickListener {
            showDropDown(hours,R.layout.item_recycler_dropdown_time,binding.alertHourTv)
        }
        //분 설정
        binding.alertMinuteTv.setOnClickListener {
            showDropDown(minutes,R.layout.item_recycler_dropdown_time,binding.alertMinuteTv)
        }
        //정기 알림 요일 설정
        val selected = ContextCompat.getColor(requireContext(), R.color.blue_200)
        val unselected = ContextCompat.getColor(requireContext(), R.color.black_300)

        // 매일
        binding.alertEverydayTv.setOnClickListener {
            binding.alertEverydayTv.isSelected = !binding.alertEverydayTv.isSelected
            binding.alertEverydayTv.setTextColor(
                if (binding.alertEverydayTv.isSelected) selected else unselected
            )
        }

        // 월요일
        binding.alertMondayTv.setOnClickListener {
            binding.alertMondayTv.isSelected = !binding.alertMondayTv.isSelected
            binding.alertMondayTv.setTextColor(
                if (binding.alertMondayTv.isSelected) selected else unselected
            )
        }

        // 화요일
        binding.alertTuesdayTv.setOnClickListener {
            binding.alertTuesdayTv.isSelected = !binding.alertTuesdayTv.isSelected
            binding.alertTuesdayTv.setTextColor(
                if (binding.alertTuesdayTv.isSelected) selected else unselected
            )
        }

        // 수요일
        binding.alertWednesdayTv.setOnClickListener {
            binding.alertWednesdayTv.isSelected = !binding.alertWednesdayTv.isSelected
            binding.alertWednesdayTv.setTextColor(
                if (binding.alertWednesdayTv.isSelected) selected else unselected
            )
        }

        // 목요일
        binding.alertThursdayTv.setOnClickListener {
            binding.alertThursdayTv.isSelected = !binding.alertThursdayTv.isSelected
            binding.alertThursdayTv.setTextColor(
                if (binding.alertThursdayTv.isSelected) selected else unselected
            )
        }

        // 금요일
        binding.alertFridayTv.setOnClickListener {
            binding.alertFridayTv.isSelected = !binding.alertFridayTv.isSelected
            binding.alertFridayTv.setTextColor(
                if (binding.alertFridayTv.isSelected) selected else unselected
            )
        }

        // 토요일
        binding.alertSaturdayTv.setOnClickListener {
            binding.alertSaturdayTv.isSelected = !binding.alertSaturdayTv.isSelected
            binding.alertSaturdayTv.setTextColor(
                if (binding.alertSaturdayTv.isSelected) selected else unselected
            )
        }

        // 일요일
        binding.alertSundayTv.setOnClickListener {
            binding.alertSundayTv.isSelected = !binding.alertSundayTv.isSelected
            binding.alertSundayTv.setTextColor(
                if (binding.alertSundayTv.isSelected) selected else unselected
            )
        }
    }
    //첫 방문인 경우 알림설정 관련 팝업 출력
    private fun showPopup(){
        val dialog = Dialog(requireActivity())
        val dialogBinding = PopupPushAlertSettingBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            // 중요: 기본 패딩 제거
            decorView.setPadding(0, 0, 0, 0)
            // 가로 풀폭 + 하단 정렬
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
        }
        dialog.show()

        //아니오 클릭
        dialogBinding.noButton.setOnClickListener {
            isFirst = false
            dialog.dismiss()
            //경우에 따라 홈 또는 온보딩으로 이동
            val intent = Intent(context as GoalActivity, MainActivity::class.java)
            startActivity(intent)
//            (context as GoalActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.goal_container,HomeFragment())
//                .commitAllowingStateLoss()
        }
        //네 클릭
        dialogBinding.yesButton.setOnClickListener {
            dialog.dismiss()
        }
        //외부 터치 시 팝업 종료
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }
    //알림설정 시간 드롭다운
    //드롭다운에 사용할 아이템, 드롭다운에 사용할 레이아웃, 앵커 뷰를 전달받음
    private fun showDropDown(items: ArrayList<String>, layout: Int, view: TextView){
        val inflater = LayoutInflater.from(context)

        val popupView = when (layout) {
            R.layout.item_recycler_dropdown_morining -> ItemRecyclerDropdownMoriningBinding.inflate(inflater).root
            R.layout.item_recycler_dropdown_time -> ItemRecyclerDropdownTimeBinding.inflate(inflater).root
            else -> throw IllegalArgumentException("Invalid layout ID")
        }

        var popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        //외부 터치 시 드롭다운 사라지게 하기 위한 설정
        popupWindow.setBackgroundDrawable(resources.getColor(R.color.transparent).toDrawable())//배경 투명하게 설정
        popupWindow.isOutsideTouchable = true //외부 터치 시 사라짐

        //앵커뷰 위에 드롭다운 표시
        //팝업 뷰 높이 측정
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupHeight = popupView.measuredHeight
        popupWindow.showAsDropDown(view, 0, -(popupHeight + view.height))

        //드롭다운 뷰에 사용되는 어댑터 생성 및 설정
        val adapter = TimerRVAdapter(items)
        adapter.setDropdownListener(object : TimerRVAdapter.DropdownListener{
            override fun setTime(position: Int) {
                view.text = items[position]
                popupWindow.dismiss()
            }
        })
        popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).adapter = adapter
    }
    //설정 완료 후 토스트 메시지 출력
    private fun makeToast(){
        val inflater = LayoutInflater.from(requireContext())
        val toastBinding = ToastGreyTemplateBinding.inflate(inflater)
        toastBinding.toastGreyTemplateTv.setText(R.string.toast_alert_setting)

        val toast = Toast(context)
        toast.view = toastBinding.root
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }

    // 알림 설정을 SharedPreferences에 저장하는 함수
    private fun saveAlertSettings() {
        val prefs = requireActivity().getSharedPreferences("alert_settings", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean("receive_alert", binding.alertReceiveOnIv.visibility == View.VISIBLE)
            putBoolean("receive_regular_alert", binding.alertRegularOnIv.visibility == View.VISIBLE)
            putString("alert_time_ampm", binding.alertTimeTv.text.toString())
            putString("alert_time_hour", binding.alertHourTv.text.toString())
            putString("alert_time_minute", binding.alertMinuteTv.text.toString())

            // 요일 저장 (선택된 요일만)
            val selectedDays = arrayListOf<String>()
            if (binding.alertEverydayTv.isSelected) selectedDays.add("everyday")
            if (binding.alertMondayTv.isSelected) selectedDays.add("mon")
            if (binding.alertTuesdayTv.isSelected) selectedDays.add("tue")
            if (binding.alertWednesdayTv.isSelected) selectedDays.add("wed")
            if (binding.alertThursdayTv.isSelected) selectedDays.add("thu")
            if (binding.alertFridayTv.isSelected) selectedDays.add("fri")
            if (binding.alertSaturdayTv.isSelected) selectedDays.add("sat")
            if (binding.alertSundayTv.isSelected) selectedDays.add("sun")

            putStringSet("alert_days", selectedDays.toSet())

            apply()
        }
    }

    // 저장된 알림 설정을 불러와 UI에 적용
    private fun applySavedSettings() {
        val prefs = requireActivity().getSharedPreferences("alert_settings", Context.MODE_PRIVATE)

        // 알림 받기 설정
        val receiveAlert = prefs.getBoolean("receive_alert", true)
        binding.alertReceiveOnIv.visibility = if (receiveAlert) View.VISIBLE else View.GONE
        binding.alertReceiveOffIv.visibility = if (receiveAlert) View.GONE else View.VISIBLE

        // 정기 알림 설정
        val receiveRegularAlert = prefs.getBoolean("receive_regular_alert", true)
        binding.alertRegularOnIv.visibility = if (receiveRegularAlert) View.VISIBLE else View.GONE
        binding.alertRegularOffIv.visibility = if (receiveRegularAlert) View.GONE else View.VISIBLE

        // 시간 설정
        binding.alertTimeTv.text = prefs.getString("alert_time_ampm", times[0])
        binding.alertHourTv.text = prefs.getString("alert_time_hour", hours[0])
        binding.alertMinuteTv.text = prefs.getString("alert_time_minute", minutes[0])

        // 요일 설정
        val selectedDays = prefs.getStringSet("alert_days", null)
        if (selectedDays != null) {
            val selected = ContextCompat.getColor(requireContext(), R.color.blue_200)
            val unselected = ContextCompat.getColor(requireContext(), R.color.black_300)

            binding.alertEverydayTv.isSelected = selectedDays.contains("everyday")
            binding.alertEverydayTv.setTextColor(if (binding.alertEverydayTv.isSelected) selected else unselected)

            binding.alertMondayTv.isSelected = selectedDays.contains("mon")
            binding.alertMondayTv.setTextColor(if (binding.alertMondayTv.isSelected) selected else unselected)

            binding.alertTuesdayTv.isSelected = selectedDays.contains("tue")
            binding.alertTuesdayTv.setTextColor(if (binding.alertTuesdayTv.isSelected) selected else unselected)

            binding.alertWednesdayTv.isSelected = selectedDays.contains("wed")
            binding.alertWednesdayTv.setTextColor(if (binding.alertWednesdayTv.isSelected) selected else unselected)

            binding.alertThursdayTv.isSelected = selectedDays.contains("thu")
            binding.alertThursdayTv.setTextColor(if (binding.alertThursdayTv.isSelected) selected else unselected)

            binding.alertFridayTv.isSelected = selectedDays.contains("fri")
            binding.alertFridayTv.setTextColor(if (binding.alertFridayTv.isSelected) selected else unselected)

            binding.alertSaturdayTv.isSelected = selectedDays.contains("sat")
            binding.alertSaturdayTv.setTextColor(if (binding.alertSaturdayTv.isSelected) selected else unselected)

            binding.alertSundayTv.isSelected = selectedDays.contains("sun")
            binding.alertSundayTv.setTextColor(if (binding.alertSundayTv.isSelected) selected else unselected)
        }
    }
}
