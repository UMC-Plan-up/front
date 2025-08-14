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
import com.example.planup.main.home.ui.HomeFragment
import androidx.core.graphics.drawable.toDrawable
import com.example.planup.goal.GoalActivity

class ChallengeSetAlertFragment : Fragment() {
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
            makeToast()
            if (isFirst) {//첫 방문인 경우 온보딩 페이지로 이동
                isFirst = false
                val intent = Intent((context as GoalActivity), MainActivity::class.java)
                startActivity(intent)
            } else {//첫 방문이 아닌 경우 홈 페이지로 이동
                val intent = Intent((context as GoalActivity), MainActivity::class.java)
                startActivity(intent)
            }
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
        val selected = ContextCompat.getColor(context, R.color.blue_200)
        val unselected = ContextCompat.getColor(context, R.color.black_300)

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
        val dialog = Dialog(context as GoalActivity)
        dialog.setContentView(R.layout.popup_push_alert)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(resources.getColor(R.color.transparent).toDrawable())
        }
        //아니오 클릭
        dialog.findViewById<TextView>(R.id.popup_push_no_btn).setOnClickListener {
            isFirst = false
            dialog.dismiss()
            //경우에 따라 홈 또는 온보딩으로 이동
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,HomeFragment())
                .commitAllowingStateLoss()
        }
        //네 클릭
        dialog.findViewById<TextView>(R.id.popup_push_yes_btn).setOnClickListener {
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
        //드롭다운 레이아웃 적용 후 rvAdapter 적용
        val popupView = inflater.inflate(layout,null)
        //드롭다운에 사용할 팝업 만들기

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
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).setText(R.string.toast_alert_setting)

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }
}
