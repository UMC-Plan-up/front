package com.example.planup.goal.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentTimerSettingBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.TimerRVAdapter
import com.example.planup.databinding.ItemRecyclerDropdownTimeBinding

class TimerSettingFragment : Fragment() {
    lateinit var binding: FragmentTimerSettingBinding
    private var totalTime = 0
    lateinit var hours: ArrayList<String> // 시
    lateinit var minutes: ArrayList<String> // 분
    lateinit var seconds: ArrayList<String> // 초

    // SharedPreferences 추가
    private val PREFS_NAME = "goal_data"
    private val KEY_GOAL_TIME = "last_goal_time"
    private val KEY_VERIFICATION_TYPE = "last_verification_type"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerSettingBinding.inflate(inflater, container, false)
        init()
        clickListener()
        updateNextButtonUi(false)
        return binding.root
    }

    // 프레그먼트 초기화
    private fun init() {
        hours = resources.getStringArray(R.array.dropdown_hour).toCollection(ArrayList<String>())
        minutes = resources.getStringArray(R.array.dropdown_minute_second).toCollection(ArrayList<String>())
        seconds = resources.getStringArray(R.array.dropdown_minute_second).toCollection(ArrayList<String>())
    }

    private fun clickListener() {
        // 이전 버튼 -> 인증방식 설정 페이지로 이동
        binding.backIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, CertificationMethodFragment())
                .commitAllowingStateLoss()
        }

        // 타이머 시간 설정
        binding.challengeTimerHourTv.setOnClickListener { // 시간
            showDropdown(hours, binding.challengeTimerHourTv, 0)
        }
        binding.challengeTimerMinuteTv.setOnClickListener { // 분
            showDropdown(minutes, binding.challengeTimerMinuteTv, 1)
        }
        binding.challengeTimerSecondTv.setOnClickListener { // 초
            showDropdown(seconds, binding.challengeTimerSecondTv, 2)
        }

        // 다음 버튼 -> GoalDetailFragment로 이동
        binding.challengeTimerNextBtn.setOnClickListener {
            if (!binding.challengeTimerNextBtn.isActivated && !binding.challengeTimerNextBtn.isEnabled) {
                return@setOnClickListener
            }

            val activity = requireActivity() as GoalActivity
            activity.goalTime = totalTime // 타이머 총 시간 저장(초)
            activity.verificationType = "TIMER" // 인증 방식 저장

            // SharedPreferences에 저장
            val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putInt(KEY_GOAL_TIME, totalTime)
                .putString(KEY_VERIFICATION_TYPE, "TIMER")
                .apply()

            val goalDetailFragment = GoalDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", activity.goalOwnerName)
                }
            }

            activity.navigateToFragment(goalDetailFragment)
        }
    }

    // 타이머로 설정한 시간 업데이트
    private fun timeWatcher(item: Int, position: Int) {
        // 기존 총 시간에서 해당 부분 시간을 빼고 새로운 시간을 더함
        val hour = (totalTime / 3600) * 3600
        val minute = ((totalTime - hour) / 60) * 60
        val second = totalTime - hour - minute

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

        /* 다음 버튼 활성화 조건 */
        val secondsPart = totalTime % 60
        val enabled = (totalTime >= 60) || (secondsPart >= 30)

        if (!enabled) {
            binding.errorTv.visibility = View.VISIBLE
        } else {
            binding.errorTv.visibility = View.GONE
        }
        updateNextButtonUi(enabled)
    }

    // 버튼 활성/비활성
    private fun updateNextButtonUi(enabled: Boolean) {
        binding.challengeTimerNextBtn.isEnabled = enabled
        binding.challengeTimerNextBtn.isActivated = enabled
        val bg = if (enabled) {
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        } else {
            // 버튼 비활성
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background_gray)
        }
        binding.challengeTimerNextBtn.background = bg
    }

    private fun showDropdown(
        items: ArrayList<String>,
        view: TextView,
        selected: Int
    ) { // 리사이클러 뷰 아이템, 앵커 뷰, 시/분/초
        val inflater = LayoutInflater.from(context)
        val popupBinding = ItemRecyclerDropdownTimeBinding.inflate(inflater)
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val dropdownAdapter = TimerRVAdapter(items)
        popupWindow.showAsDropDown(view) // 선택된 뷰 하단에 드롭다운 표시
        popupWindow.isOutsideTouchable = true // 바깥 터치 허용
        popupWindow.setBackgroundDrawable(
            resources.getColor(R.color.transparent).toDrawable()
        ) // 투명 배경 설정

        // 드롭다운 터치 이벤트 관리하는 어댑터
        popupBinding.dropdownRecyclerRv.adapter = dropdownAdapter
        dropdownAdapter.setDropdownListener(object : TimerRVAdapter.DropdownListener {
            override fun setTime(position: Int) {
                view.text = items[position]
                timeWatcher(items[position].toInt(), selected)
                popupWindow.dismiss()
            }
        })
    }
}