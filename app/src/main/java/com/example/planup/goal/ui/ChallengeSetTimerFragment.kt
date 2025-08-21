package com.example.planup.goal.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetTimerBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.TimerRVAdapter
import androidx.core.graphics.drawable.toDrawable

class ChallengeSetTimerFragment : Fragment() {
    lateinit var binding: FragmentChallengeSetTimerBinding
    private var totalTime = 0
    lateinit var hours: ArrayList<String>// 시
    lateinit var minutes: ArrayList<String>// 분
    lateinit var seconds: ArrayList<String>// 초

    lateinit var prefs: SharedPreferences //챌린지 정보 저장을 위한 sharedPreferences
    lateinit var editor: SharedPreferences.Editor //sharedPreferences editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetTimerBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    //프레그먼트 초기화
    private fun init() {
        binding.challengeSetTimerCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val height = binding.challengeSetTimerCl.height
                binding.challengeSetTimerInnerCl.minHeight = height
                binding.challengeSetTimerCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
        hours =
            resources.getStringArray(R.array.dropdown_hour).toCollection(ArrayList<String>()) //시간
        minutes = resources.getStringArray(R.array.dropdown_minute_second)
            .toCollection(ArrayList<String>()) //분
        seconds = resources.getStringArray(R.array.dropdown_minute_second)
            .toCollection(ArrayList<String>()) //초
        //챌린지 정보를 저장할 sharedPreferences 생성
        prefs = (context as GoalActivity).getSharedPreferences("challenge", MODE_PRIVATE)
        editor = prefs.edit()
    }

    private fun clickListener() {
        //이전 버튼 -> 인증방식 설정 페이지로 이동
        binding.backIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, ChallengeTimerPhotoFragment())
                .commitAllowingStateLoss()
        }

        //타이머 시간 설정
        //시간 드롭다운
        binding.challengeTimerHourTv.setOnClickListener {
            showDropdown(hours, binding.challengeTimerHourTv, 0)
        }
        //분 드롭다운
        binding.challengeTimerMinuteTv.setOnClickListener {
            showDropdown(minutes, binding.challengeTimerMinuteTv, 1)
        }
        //초 드롭다운
        binding.challengeTimerSecondTv.setOnClickListener {
            showDropdown(seconds, binding.challengeTimerSecondTv, 2)
        }

        //다음 버튼 -> 페널티 설정 페이지로 이동
        binding.challengeTimerNextBtn.setOnClickListener {
            if (!binding.challengeTimerNextBtn.isActivated) return@setOnClickListener
            editor.apply()
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, ChallengeSetFrequencyFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }


    //타이머로 설정한 시간 업데이트
    //마지막 조건문으로 전체 시간이 30초 이상인지 확인
    private fun timeWatcher(item: Int, position: Int) {
        val hour = (totalTime / 3600) * 3600
        val minute = ((totalTime - (totalTime / 3600) * 3600) / 60) * 60
        val second =
            totalTime - (totalTime / 3600) * 3600 - ((totalTime - (totalTime / 3600) * 3600) / 60) * 60

        if (position == 0) {
            totalTime -= hour
            totalTime += 3600 * item
        } else if (position == 1) {
            totalTime -= minute
            totalTime += 60 * item
        } else if (position == 2) {
            totalTime -= second
            totalTime += item
        }
        if (totalTime < 30) {
            binding.errorTv.visibility = View.VISIBLE
        } else {
            binding.errorTv.visibility = View.GONE
            editor.putInt("targetTime",totalTime)
            binding.challengeTimerNextBtn.isActivated = true
        }
    }

    //시간, 분, 초 드롭다운
    private fun showDropdown(
        items: ArrayList<String>, //시간
        view: TextView, //앵커뷰
        selected: Int //시, 분, 초 중 어느 드롭다운인지 표시
    ) {//리사이클러 뷰 아이템, 앵커 뷰, 시/분/초
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.item_recycler_dropdown_time, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val dropdownAdapter = TimerRVAdapter(items)
        popupWindow.showAsDropDown(view) //선택된 뷰 하단에 드롭다운 표시
        popupWindow.isOutsideTouchable = true //바깥 터치 허용
        popupWindow.setBackgroundDrawable(
            resources.getColor(R.color.transparent).toDrawable()
        )//투명 배경 설정

        //드롭다운 터치 이벤트 관리하는 어댑터
        popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).adapter = dropdownAdapter
        dropdownAdapter.setDropdownListener(object : TimerRVAdapter.DropdownListener {
            override fun setTime(position: Int) {
                view.text = items[position]
                timeWatcher(items[position].toInt(), selected)
                popupWindow.dismiss()
            }
        })
    }
}