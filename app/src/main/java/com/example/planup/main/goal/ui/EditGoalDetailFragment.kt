package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditGoalDetailFragment : Fragment(){
    private lateinit var binding: FragmentEditGoalDetailBinding
    private var goalId: Int = 0
    private var goalName: String = ""
    private var goalAmount: String = ""
    private var goalCategory: String = ""
    private var goalType: String = ""
    private var oneDose: Int = 0
    private var frequency: Int = 0
    private var period: String = ""
    private var endDate: String = ""
    private var verificationType: String = ""
    private var limitFriendCount: Int = 0
    private var goalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getInt("goalId") ?: 0
        goalName = arguments?.getString("goalName") ?: ""
        goalAmount = arguments?.getString("goalAmount") ?: ""
        goalCategory = arguments?.getString("goalCategory") ?: ""
        goalType = arguments?.getString("goalType") ?: ""
        oneDose = arguments?.getInt("oneDose") ?: 0
        frequency = arguments?.getInt("frequency") ?: 0
        period = arguments?.getString("period") ?: ""
        endDate = arguments?.getString("endDate") ?: ""
        verificationType = arguments?.getString("verificationType") ?: ""
        limitFriendCount = arguments?.getInt("limitFriendCount") ?: 0
        goalTime = arguments?.getInt("goalTime") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditGoalDetailBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_edit_goal_detail, container, false)

        val dailyBtn = binding.editDetailDayDailyBtn
        val weeklyBtn = binding.editDetailDayWeeklyBtn
        val monthlyBtn = binding.editDetailDayMonthlyBtn
        val frequencyEt = binding.editDetailFrequencyEt
        val nextBtn = binding.editDetailNextBtn
        val backBtn = binding.editDetailBackIv


        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val freqStr = frequencyEt.text.toString()

        // 버튼들을 리스트로 묶어 관리
        val buttonList = listOf(dailyBtn, weeklyBtn, monthlyBtn)
        var duration: String = period
        fun selectButton(selectedBtn: AppCompatButton) {
            for (button in buttonList) {
                if (button == selectedBtn) {
                    // 선택된 버튼 스타일
                    button.setBackgroundResource(R.drawable.btn_yes)
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.blue_200))
                } else {
                    // 나머지 버튼은 비활성화 스타일
                    button.setBackgroundResource(R.drawable.btn_no)
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.black_300))
                }
            }
            duration = when (selectedBtn) {
                dailyBtn -> "DAY"
                weeklyBtn -> "WEEK"
                monthlyBtn -> "MONTH"
                else -> "DAY"
            }
            binding.frequencyInputState.text = selectedBtn.text
        }



// 클릭 리스너 설정
        dailyBtn.setOnClickListener { selectButton(dailyBtn) }
        weeklyBtn.setOnClickListener { selectButton(weeklyBtn) }
        monthlyBtn.setOnClickListener { selectButton(monthlyBtn) }

        nextBtn.setOnClickListener {
            val freqValue = freqStr.toIntOrNull() ?: frequency

            val nextFragment = PushAlertEditFragment()
            val bundle = Bundle().apply {
                putInt("goalId", goalId)
                putString("goalName", goalName)
                putString("goalAmount", goalAmount)
                putString("goalCategory", goalCategory)
                putString("goalType", goalType)
                putInt("oneDose", oneDose)
                putInt("frequency", freqValue)
                putString("period", duration)
                putString("endDate", endDate)
                putString("verificationType", verificationType)
                putInt("limitFriendCount", limitFriendCount)
                putInt("goalTime", goalTime)
            }
            nextFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }


}