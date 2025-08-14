package com.example.planup.goal.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetFrequencyBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.TimerRVAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChallengeSetFrequencyFragment : Fragment() {
    lateinit var binding: FragmentChallengeSetFrequencyBinding

    //선택된 종료일 파란색으로 표시하기 위한 변수
    private lateinit var selectBackground: View //선택된 요일의 프레임
    private lateinit var selectText: TextView //선택된 요일의 텍스트
    //종료일을 선택하지 않은 경우
    private var isFirst: Boolean = true
    //선택 가능한 종료일을 계산하기 위한 변수
    private lateinit var calendar: Calendar
    //기준일 설정 드롭다운
    private lateinit var popupWindow: PopupWindow
    //종료일에 따라 드롭다운에 표시되는 기준기간이 결정됨
    private var endDay: Int = 0
    //선택버튼 활성화를 위한 조건 설정
    private var finish: Boolean = false //종료일 설정 여부
    private var duration: Boolean = false //기준 기간 설정 여부
    private var often: Boolean = false //빈도 설정 여부
    //챌린지 설정 정보 저장
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetFrequencyBinding.inflate(inflater, container, false)
        init()
        clickListener()
        textListener()
        return binding.root
    }
    private fun init(){
        //prevFragment = arguments?.getString("previous","null").toString()
        prefs = (context as GoalActivity).getSharedPreferences("challenge",MODE_PRIVATE)
        editor = prefs.edit()
        //종료일 설정을 위한 날짜 연동
        val sdf = SimpleDateFormat("MM/dd (E)", Locale.KOREAN)
        calendar = Calendar.getInstance()

        //다음날부터 최대 7일째까지 선택 가능
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val dayTextViews = listOf(
            binding.photoFirstDayTv,
            binding.photoSecondDayTv,
            binding.photoThirdDayTv,
            binding.photoFourthDayTv,
            binding.photoFifthDayTv,
            binding.photoSixthDayTv,
            binding.photoSeventhDayTv
        )
        for (tv in dayTextViews) {
            tv.text = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun clickListener() {

        //뒤로가기: 타이머 설정 또는 인증방식 선택 페이지로 이동
        binding.photoBackIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.popBackStack()
        }
        /* 요일 선택 효과 */
        // 월요일
        binding.photoFirstDayCl.setOnClickListener {
            if (binding.photoFirstDayCl.isSelected) return@setOnClickListener
            setEndDay(
                binding.photoFirstDayCl,
                binding.photoFirstDayTv,
                0
            )
        }

        // 화요일
        binding.photoSecondDayCl.setOnClickListener {
            if (binding.photoSecondDayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoSecondDayCl,
                binding.photoSecondDayTv,
                1)
        }

        // 수요일
        binding.photoThirdDayCl.setOnClickListener {
            if (binding.photoThirdDayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoThirdDayCl,
                binding.photoThirdDayTv,
                2)
        }

        // 목요일
        binding.photoFourthDayCl.setOnClickListener {
            if (binding.photoFourthDayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoFourthDayCl,
                binding.photoFourthDayTv,
                3)
        }

        // 금요일
        binding.photoFifthDayCl.setOnClickListener {
            if (binding.photoFifthDayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoFifthDayCl,
                binding.photoFifthDayTv,
                4)
        }

        // 토요일
        binding.photoSixthDayCl.setOnClickListener {
            if (binding.photoSixthDayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoSixthDayCl,
                binding.photoSixthDayTv,
                5)
        }


        // 일요일
        binding.photoSeventhDayCl.setOnClickListener {
            if (binding.photoSeventhDayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoSeventhDayCl,
                binding.photoSeventhDayTv,
                6)
        }

        //기준 기간
        binding.photoDurationCl.setOnClickListener {
            setDuration(binding.photoDurationCl, endDay)
        }

        //다음 버튼 클릭: 페널티 설정 화면으로 이동
        binding.btnNextTv.setOnClickListener {
            if (!binding.btnNextTv.isActivated) return@setOnClickListener
            editor.apply()
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, ChallengePenaltyFragment())
                .commitAllowingStateLoss()
        }
    }

    //종료일 설정
    private fun setEndDay(background: View, text: TextView, endDay: Int) {
        //선택된 종료일 파란색으로 표시
        //기존 종료일은 회색으로 변경
        val selected = ContextCompat.getColor(context, R.color.blue_200)
        val unselected = ContextCompat.getColor(context, R.color.black_300)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREAN)
        //이미 다른 요일을 선택한 경우
        if (!isFirst) { //이전 선택 요일 해제
            selectBackground.isSelected = !selectBackground.isSelected
            selectText.setTextColor(unselected)
        }
        isFirst = false

        //현재 선택 요일 변경
        selectBackground = background
        selectText = text
        //선택 요일 표시
        selectBackground.isSelected = !selectBackground.isSelected
        selectText.setTextColor(selected)
        //선택된 종료일 계산
        calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,endDay)
        //선택된 종료일 저장
        this.endDay = endDay
        editor.putString("endDate",sdf.format(calendar.time))
        finish = true

        binding.btnNextTv.isActivated = finish && duration && often //다음 버튼 활성화 여부 확인
    }
    //빈도 설정
    private fun textListener() {
        binding.photoOftenEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.photoOftenEt.text.isNotEmpty()
                    && binding.photoOftenEt.text.toString().toInt() >= 1
                ) {
                    binding.photoErrorTv.visibility = View.GONE
                    binding.photoErrorIv.visibility = View.GONE
                    //빈도 저장
                    editor.putInt("frequency",binding.photoOftenEt.text.toString().toInt())
                    often = true
                } else {
                    binding.photoErrorTv.visibility = View.VISIBLE
                    binding.photoErrorIv.visibility = View.VISIBLE
                    often = false
                }
                binding.btnNextTv.isActivated = finish && duration && often //다음 버튼 활성화 여부 확인
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }
    //기준 기간 설정
    private fun setDuration(view: View, day: Int) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.item_recycler_dropdown_duration, null)
        val durations = resources.getStringArray(R.array.dropdown_duration).toCollection(ArrayList<String>())

        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAsDropDown(view)
        //배경을 설정하고 외부 터치를 허용해서 외부 터치 시 드롭다운 사라질 수 있게 함
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ContextCompat.getColor(context,R.color.transparent).toDrawable())

        //기준 기간 선택하는 드롭다운의 어댑터, 종료일자에 따라 선택 가능한 기준기간 달라짐
        val adapter = TimerRVAdapter(durations.subList(0,day+1).toCollection(ArrayList<String>()))
        adapter.setDropdownListener(object : TimerRVAdapter.DropdownListener{
            override fun setTime(position: Int) {
                binding.photoDurationTv.text = durations[position]
                duration = true
                popupWindow.dismiss()
                //기준 기간 저장
                editor.putString("timePerPeriod",durations[position])
            }
        })
        popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).adapter = adapter

        binding.btnNextTv.isActivated = finish && duration && often //다음 버튼 활성화 여부 확인
    }
}