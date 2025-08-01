package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentTimerSettingBinding
import com.example.planup.goal.GoalActivity

class TimerSettingFragment : Fragment() {

    private lateinit var hourEditText: EditText
    private lateinit var minuteEditText: EditText
    private lateinit var secondEditText: EditText

    private lateinit var nextButton: AppCompatButton
    private lateinit var backIcon: ImageView
    private lateinit var binding: FragmentTimerSettingBinding
    private var totalTime = 0 //타이머 설정 시간을 초 단위로 저장

    private var goalOwnerName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer_setting, container, false)

        goalOwnerName = arguments?.getString("goalOwnerName")
            ?: throw IllegalStateException("TimerSettingFragment must receive goalOwnerName!")

        initViews(view)
        setupNextButton()
        setupBackButton()

        return view
    }

    /* 뷰 초기화 */
    private fun initViews(view: View) {
        hourEditText = view.findViewById(R.id.hourEditText)
        minuteEditText = view.findViewById(R.id.minuteEditText)
        secondEditText = view.findViewById(R.id.secondEditText)

        nextButton = view.findViewById(R.id.nextButton)
        backIcon = view.findViewById(R.id.backIcon)

        // 버튼은 항상 활성화
        nextButton.isEnabled = true
        nextButton.setBackgroundResource(R.drawable.btn_next_background)
    }

    /* 다음 버튼 클릭 → GoalDetailFragment로 이동 */
    private fun setupNextButton() {
        nextButton.setOnClickListener {
            val goalDetailFragment = GoalDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                    putString("SELECTED_METHOD", "TIMER")
                }
            }

            (requireActivity() as GoalActivity).navigateToFragment(goalDetailFragment)
        }
    }

    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
    private fun setupBackButton() {
        backIcon.setOnClickListener {
            val certFragment = CertificationMethodFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }

        }
        timerAdapter[1].setDropdownListener(object : TimerRVAdapter.DropdownListener{
            //분 선택
            override fun setTime(position: Int) {
                binding.challengeTimerMinuteTv.text = minutes[position]
                binding.challengeTimerMinuteRv.visibility = View.GONE
                timeWatcher(minutes[position].toInt(),1) //전체 시간 업데이트
            }
        })
        timerAdapter[2].setDropdownListener(object : TimerRVAdapter.DropdownListener{
            //초 선택
            override fun setTime(position: Int) {
                binding.challengeTimerSecondTv.text = seconds[position]
                binding.challengeTimerSecondRv.visibility = View.GONE
                timeWatcher(seconds[position].toInt(),2) //전체 시간 업데이트
            }
        })
    }
    //타이머로 설정한 시간 업데이트
    //마지막 조건문으로 전체 시간이 30초 이상인지 확인
    private fun timeWatcher(selected:Int,position:Int){
        val hour = (totalTime / 3600) * 3600
        val minute = ((totalTime - (totalTime / 3600) * 3600) / 60) * 60
        val second= totalTime - ((totalTime - (totalTime / 3600) * 3600) / 60) * 60

        if (position == 0){
            totalTime -= hour
            totalTime += 3600*selected
        } else if (position == 1){
            totalTime -= minute
            totalTime += 60*selected
        } else if (position == 2){

            totalTime -= second
            totalTime += selected
        }
        if (totalTime < 30){
            binding.errorTv.visibility = View.VISIBLE
            binding.challengeTimerNextBtn.isActivated = false
        }else{
            binding.errorTv.visibility = View.GONE
            binding.challengeTimerNextBtn.isActivated = true

        }
    }
}