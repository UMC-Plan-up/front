package com.example.planup.main.goal.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalTimerBinding
import com.example.planup.databinding.FragmentTimerSettingBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.TimerRVAdapter
import com.example.planup.goal.ui.GoalDetailFragment
import com.example.planup.goal.util.backStackTrueGoalNav
import com.example.planup.goal.util.backStackTrueNav
import com.example.planup.goal.util.clockString
import com.example.planup.goal.util.goalType
import com.example.planup.goal.util.setInsets
import com.example.planup.goal.util.titleFormat
import com.example.planup.main.goal.viewmodel.GoalViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections.frequency

@AndroidEntryPoint
class EditGoalTimerFragment : Fragment() {
    private lateinit var binding: FragmentTimerSettingBinding

    lateinit var hours: ArrayList<String> // 시
    lateinit var minutes: ArrayList<String> // 분
    lateinit var seconds: ArrayList<String>
    private lateinit var nextBtn: AppCompatButton
    private lateinit var warningTv: TextView
    private var totalTime = 0

    private val viewModel: GoalViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 1️⃣ binding 초기화
        binding = FragmentTimerSettingBinding.inflate(inflater, container, false)
//        if (totalTime > 29) {
//            setTime()
//        }

        binding.titleTv.text = getString(R.string.goal_friend_together_detail_title)

        binding.backIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 3️⃣ Spinner 항목 리스트 생성
//        val hourList = (0..23).map { it.toString().padStart(2, '0') }
//        val minuteSecondList = (0..59).map { it.toString().padStart(2, '0') }
//        val secondList = (0..59).map { it.toString().padStart(2, '0') }
        hours = resources.getStringArray(R.array.dropdown_hour).toCollection(ArrayList<String>())
        minutes = resources.getStringArray(R.array.dropdown_minute_second)
            .toCollection(ArrayList<String>())
        seconds = resources.getStringArray(R.array.dropdown_minute_second)
            .toCollection(ArrayList<String>())

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
            if (!binding.challengeTimerNextBtn.isActivated) {
                return@setOnClickListener
            }

            viewModel.setGoalData(
                viewModel.goalData.copy(
                    goalTime = totalTime
                )
            )
            Log.d("TimerSettingFragment", "goalTime: ${viewModel.goalData}")


            // SharedPreferences에 저장
//            val bundle = Bundle().apply {
//                putInt("goalId", goalId)
//                putString("goalName", goalName)
//                putString("goalAmount", goalAmount)
//                putString("goalCategory", goalCategory)
//                putString("goalType", goalType)
//                putInt("oneDose", oneDose)
//                putInt("frequency", frequency)
//                putString("period", period)
//                putString("endDate", endDate)
//                putString("verificationType", verificationType)
//                putInt("limitFriendCount", limitFriendCount)
//                putInt("goalTime", totalTime)
//            }

            val nextFragment = EditGoalDetailFragment()
            backStackTrueNav(R.id.edit_friend_goal_fragment_container,nextFragment)
        }
//        nextBtn.setOnClickListener {
//            val hour = hourSpinner.selectedItem.toString().toIntOrNull() ?: 0
//            val minute = minuteSpinner.selectedItem.toString().toIntOrNull() ?: 0
//            val second = secondSpinner.selectedItem.toString().toIntOrNull() ?: 0
//            val totalSeconds = hour * 3600 + minute * 60 + second
//
//
//        }

//        updateNextButtonState()

        // 4️⃣ binding.root 리턴
        return binding.root
    }




//    private fun updateNextButtonState() {
//        val hour = hourSpinner.selectedItem.toString().toIntOrNull() ?: 0
//        val minute = minuteSpinner.selectedItem.toString().toIntOrNull() ?: 0
//        val second = secondSpinner.selectedItem.toString().toIntOrNull() ?: 0
//
//        val totalSeconds = hour * 3600 + minute * 60 + second
//        val isTimeEnough = totalSeconds >= 30
//
//        nextBtn.isEnabled = isTimeEnough
//
//        if (isTimeEnough) {
//            nextBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_300))
//            warningTv.visibility = View.GONE
//        } else {
//            nextBtn.setBackgroundResource(R.drawable.btn_next_background_gray)
//            warningTv.visibility = View.VISIBLE
//        }
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setInsets(binding.root)
    Log.d("TimerSettingFragment", "goalData: ${viewModel.editGoalData!!.goalTime}")
        viewModel.setGoalData(
            viewModel.goalData.copy(
                goalTime = viewModel.editGoalData!!.goalTime
            )
        )
        Log.d("TimerSettingFragment", "goalData: ${viewModel.goalData}")

    }

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
                when(selected){
                    0 -> view.text = getString(R.string.timer_hour, items[position])
                    1 -> view.text = getString(R.string.timer_minute,items[position])
                    2 -> view.text = getString(R.string.timer_second,items[position])
                }
                timeWatcher(items[position].toInt(), selected)
                popupWindow.dismiss()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        totalTime = viewModel.goalData.goalTime
        if (totalTime > 29)
            setTime()
        else
            Log.d("TimerSettingFragment", "totalTime: $totalTime")

    }

    private fun timeWatcher(item: Int, position: Int) {
        // 기존 총 시간에서 해당 부분 시간을 빼고 새로운 시간을 더함
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

        /* 다음 버튼 활성화 조건 */
        if (totalTime < 30) {
            binding.errorTv.visibility = View.VISIBLE
            binding.challengeTimerNextBtn.isActivated = false
        } else {
            binding.errorTv.visibility = View.GONE
            binding.challengeTimerNextBtn.isActivated = true
        }
    }

    private fun setTime(){
        val time = totalTime
        Log.d("TimerSettingFragment", "time: $time")
        // 기존 총 시간에서 해당 부분 시간을 빼고 새로운 시간을 더함
        val hour = time / 3600
        val minute = (time % 3600) / 60
        val second = time % 60
        binding.challengeTimerHourTv.text =
            getString(R.string.timer_hour, hour.clockString())
        binding.challengeTimerMinuteTv.text =
            getString(R.string.timer_minute, minute.clockString())
        binding.challengeTimerSecondTv.text =
            getString(R.string.timer_second, second.clockString())


    }
}
