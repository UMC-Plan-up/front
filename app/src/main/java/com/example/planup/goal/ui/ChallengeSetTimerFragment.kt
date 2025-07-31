package com.example.planup.goal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetTimerBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.TimerRVAdapter
import java.sql.Array
import java.sql.Time

class ChallengeSetTimerFragment:Fragment() {
    lateinit var binding: FragmentChallengeSetTimerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetTimerBinding.inflate(inflater,container,false)
        clickListener()
        setTimer()
        return binding.root
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
            binding.challengeTimerHourRv.visibility = View.VISIBLE
        }
        binding.challengeTimerMinuteTv.setOnClickListener { //분
            binding.challengeTimerMinuteRv.visibility = View.VISIBLE
        }
        binding.challengeTimerSecondTv.setOnClickListener { //초
            binding.challengeTimerSecondRv.visibility = View.VISIBLE
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
    private fun setTimer(){
        //시간, 분, 초 를 저장하는 어레이 리스트 만들기
        val hours = resources.getStringArray(R.array.spinner_hour).toCollection(ArrayList<String>()) //시간
        val minutes = resources.getStringArray(R.array.spinner_minute_second).toCollection(ArrayList<String>()) //분
        val seconds = resources.getStringArray(R.array.spinner_minute_second).toCollection(ArrayList<String>()) //초
        Log.d("afadsfadsfadshgkljdfshgiru", R.array.spinner_minute_second.toString())

        //각 드롭다운에 대한 어댑터 생성
        val timerAdapter = ArrayList<TimerRVAdapter>()
        timerAdapter.add(0, TimerRVAdapter(hours))
        timerAdapter.add(1, TimerRVAdapter(minutes))
        timerAdapter.add(2, TimerRVAdapter(seconds))

        binding.challengeTimerHourRv.adapter = timerAdapter[0]
        binding.challengeTimerMinuteRv.adapter = timerAdapter[1]
        binding.challengeTimerSecondRv.adapter = timerAdapter[2]

        timerAdapter[0].setDropdownListener(object : TimerRVAdapter.DropdownListener{
            //시간 선택
            override fun setTime(position: Int) {
                Log.d("afdfdaff","afafdfafaffffa hour touch")
                binding.challengeTimerHourTv.text = hours[position]
                binding.challengeTimerHourRv.visibility = View.GONE
            }
        })
        timerAdapter[1].setDropdownListener(object : TimerRVAdapter.DropdownListener{
            //분 선택
            override fun setTime(position: Int) {
                Log.d("afdfdaff","afafdfafaffffa minute click")
                binding.challengeTimerMinuteTv.text = minutes[position]
                binding.challengeTimerMinuteRv.visibility = View.GONE
            }
        })
        timerAdapter[2].setDropdownListener(object : TimerRVAdapter.DropdownListener{
            //초 선택
            override fun setTime(position: Int) {
                Log.d("afdfdaff","afafdfafaffffa second click")
                binding.challengeTimerSecondTv.text = seconds[position]
                binding.challengeTimerSecondRv.visibility = View.GONE
            }
        })
    }
}