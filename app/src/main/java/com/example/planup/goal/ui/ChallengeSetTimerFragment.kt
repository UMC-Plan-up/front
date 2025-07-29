package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetTimerBinding
import com.example.planup.goal.GoalActivity

class ChallengeSetTimerFragment:Fragment() {
    lateinit var binding: FragmentChallengeSetTimerBinding
    private var hour: Int = 0
    private var minute: Int = 0
    private var second: Int = 0
    private var sum: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetTimerBinding.inflate(inflater,container,false)
        clickListener()
        setTimer(binding.challengeTimerHourSp, R.array.spinner_hour) //시간 드롭다운
        setTimer(binding.challengeTimerMinuteSp, R.array.spinner_minute_second) //분 드롭다운
        setTimer(binding.challengeTimerSecondSp, R.array.spinner_minute_second) //초 드롭다운
        //sumTime()
        return binding.root
    }
    private fun clickListener(){
        //이전 버튼 -> 인증방식 설정 페이지로 이동
        binding.backIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeTimerPhotoFragment())
                .commitAllowingStateLoss()
        }
        //다음 버튼 -> 페널티 설정 페이지로 이동
        binding.challengeTimerNextBtn.setOnClickListener{
            if (!binding.challengeTimerNextBtn.isActivated) return@setOnClickListener
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengePenaltyFragment())
                .commitAllowingStateLoss()
        }
    }
    private fun setTimer(spinnerId: AppCompatSpinner, stringId: Int){
        val spinner = spinnerId
        val items = resources.getStringArray(stringId)
        val adapter = ArrayAdapter(requireContext(),R.layout.item_spinner_challenge_timer,items)
        adapter.setDropDownViewResource(R.layout.dropdown_timer)

        spinner.adapter = adapter

        spinner.setSelection(0,false)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (spinner) {
                    binding.challengeTimerHourSp -> hour = spinner.selectedItem.toString().toInt()
                    binding.challengeTimerMinuteSp -> minute = spinner.selectedItem.toString().toInt()
                    binding.challengeTimerSecondSp -> second = spinner.selectedItem.toString().toInt()
                }
//                sum = (3600 * hour) + (60 * minute) + (second)
                sumTime((3600 * hour) + (60 * minute) + (second))
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
    private fun sumTime(sum:Int){
        if (sum<30){
            binding.errorTv.text = getString(R.string.error_more_thirty_second)
            binding.errorTv.text = getString(R.string.error_more_thirty_second)
            binding.challengeTimerNextBtn.isActivated = false
        } else{
            binding.errorTv.text = null
            binding.challengeTimerNextBtn.isActivated = true
        }
    }

}