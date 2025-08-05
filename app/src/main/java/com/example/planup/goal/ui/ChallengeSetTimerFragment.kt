package com.example.planup.goal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetTimerBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.TimerRVAdapter

class ChallengeSetTimerFragment:Fragment() {
    lateinit var binding: FragmentChallengeSetTimerBinding
    private var totalTime = 0
    lateinit var hours: ArrayList<String>// 시
    lateinit var minutes: ArrayList<String>// 분
    lateinit var seconds: ArrayList<String>// 초

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetTimerBinding.inflate(inflater,container,false)
        clickListener()
        init()
        return binding.root
    }
    private fun init(){
        hours = resources.getStringArray(R.array.spinner_hour).toCollection(ArrayList<String>()) //시간
        minutes = resources.getStringArray(R.array.spinner_minute_second).toCollection(ArrayList<String>()) //분
        seconds = resources.getStringArray(R.array.spinner_minute_second).toCollection(ArrayList<String>()) //초
    }
    private fun clickListener(){
        //이전 버튼 -> 인증방식 설정 페이지로 이동
        binding.backIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeTimerPhotoFragment())
                .commitAllowingStateLoss()
        }

        //타이머 시간 설정
        binding.challengeTimerHourTv.setOnClickListener { //시간
  //          binding.challengeTimerHourRv.visibility = View.VISIBLE
            showDropdown(hours, binding.challengeTimerHourTv, 0)
        }
        binding.challengeTimerMinuteTv.setOnClickListener { //분
//            binding.challengeTimerMinuteRv.visibility = View.VISIBLE
            showDropdown(minutes, binding.challengeTimerMinuteTv,1)
        }
        binding.challengeTimerSecondTv.setOnClickListener { //초
    //        binding.challengeTimerSecondRv.visibility = View.VISIBLE
            showDropdown(seconds, binding.challengeTimerSecondTv, 2)

        }

        //다음 버튼 -> 페널티 설정 페이지로 이동
        binding.challengeTimerNextBtn.setOnClickListener{
            if (!binding.challengeTimerNextBtn.isActivated) return@setOnClickListener
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengePenaltyFragment())
                .commitAllowingStateLoss()
        }
    }

    //타이머 설정 관리
//    private fun setTimer(){
//        //시간, 분, 초 를 저장하는 어레이 리스트 만들기
//        val hours = resources.getStringArray(R.array.spinner_hour).toCollection(ArrayList<String>()) //시간
//        val minutes = resources.getStringArray(R.array.spinner_minute_second).toCollection(ArrayList<String>()) //분
//        val seconds = resources.getStringArray(R.array.spinner_minute_second).toCollection(ArrayList<String>()) //초
//
//        //각 드롭다운에 대한 어댑터 생성
//        val timerAdapter = ArrayList<TimerRVAdapter>()
//        timerAdapter.add(0, TimerRVAdapter(hours))
//        timerAdapter.add(1, TimerRVAdapter(minutes))
//        timerAdapter.add(2, TimerRVAdapter(seconds))
//
//        binding.challengeTimerHourRv.adapter = timerAdapter[0]
//        binding.challengeTimerMinuteRv.adapter = timerAdapter[1]
//        binding.challengeTimerSecondRv.adapter = timerAdapter[2]
//
//        timerAdapter[0].setDropdownListener(object : TimerRVAdapter.DropdownListener{
//            //시간 선택
//            override fun setTime(position: Int) {
//                binding.challengeTimerHourTv.text = hours[position]
//                binding.challengeTimerHourRv.visibility = View.GONE
//                timeWatcher(hours[position].toInt(),0) //전체 시간 업데이트
//            }
//        })
//        timerAdapter[1].setDropdownListener(object : TimerRVAdapter.DropdownListener{
//            //분 선택
//            override fun setTime(position: Int) {
//                binding.challengeTimerMinuteTv.text = minutes[position]
//                binding.challengeTimerMinuteRv.visibility = View.GONE
//                timeWatcher(minutes[position].toInt(),1) //전체 시간 업데이트
//            }
//        })
//        timerAdapter[2].setDropdownListener(object : TimerRVAdapter.DropdownListener{
//            //초 선택
//            override fun setTime(position: Int) {
//                binding.challengeTimerSecondTv.text = seconds[position]
//                binding.challengeTimerSecondRv.visibility = View.GONE
//                timeWatcher(seconds[position].toInt(),2) //전체 시간 업데이트
//            }
//        })
//    }

    private fun showDropdown(items: ArrayList<String>, view: TextView, selected: Int){//리사이클러 뷰 아이템, 앵커 뷰, 시/분/초
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.item_recycler_dropdown,null)
        val popupWindow = PopupWindow(
            popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val dropdownAdapter = TimerRVAdapter(items)
        popupWindow.showAsDropDown(view) //선택된 뷰 하단에 드롭다운 표시
        popupWindow.isOutsideTouchable = true //바깥 터치 허용

        popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).adapter = dropdownAdapter
        dropdownAdapter.setDropdownListener(object : TimerRVAdapter.DropdownListener {
            override fun setTime(position: Int) {
                view.text = items[position]
                timeWatcher(items[position].toInt(), selected)
                popupWindow.dismiss()
                binding.challengeTimerHourTv.text = hours[position]
                binding.challengeTimerHourRv.visibility = View.GONE
                timeWatcher(3600*hours[position].toInt(),0) //전체 시간 업데이트
            }
        })
    }
    //타이머로 설정한 시간 업데이트
    //마지막 조건문으로 전체 시간이 30초 이상인지 확인
    private fun timeWatcher(selected:Int,position:Int){
        val hour = (totalTime/3600)*3600
        val minute = (totalTime-(totalTime/3600)*3600)/60
        val second= totalTime - ((totalTime-(totalTime/3600)*3600)/60)

        if (position==0){
            totalTime -= hour
            totalTime += 3600*selected
        } else if (position == 1){
            totalTime -= minute
            totalTime += 60*selected
        } else{
            totalTime -= second
            totalTime += selected
        }
        if (totalTime < 30){
            binding.errorTv.visibility = View.VISIBLE
        }else{
            binding.errorTv.visibility = View.GONE
        }
    }
}