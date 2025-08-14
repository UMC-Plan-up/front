package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalDetailBinding

class EditGoalDetailFragment : Fragment(){
    private lateinit var binding: FragmentEditGoalDetailBinding
    private var goalId: Long = -1L
    private var title: String = ""
    private var oneDose: String = ""
    private var authType: String = ""
    private var hour: String = ""
    private var minute: String = ""
    private var second: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getLong("goalId") ?: -1L
        title = arguments?.getString("title") ?: ""
        oneDose = arguments?.getString("oneDose") ?: ""
        authType = arguments?.getString("authType") ?: ""
        hour = arguments?.getString("hour") ?: ""
        minute = arguments?.getString("minute") ?: ""
        second = arguments?.getString("second") ?: ""
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

        val frequency = frequencyEt.text.toString()

        // 버튼들을 리스트로 묶어 관리
        val buttonList = listOf(dailyBtn, weeklyBtn, monthlyBtn)
        var duration: String = "DAILY"
        fun selectButton(selectedBtn: AppCompatButton) {
            for (button in buttonList) {
                if (button == selectedBtn) {
                    // 선택된 버튼 스타일
                    button.setBackgroundResource(R.drawable.btn_yes)
                } else {
                    // 나머지 버튼은 비활성화 스타일
                    button.setBackgroundResource(R.drawable.btn_no)
                }
            }
            duration = when (selectedBtn) {
                dailyBtn -> "DAILY"
                weeklyBtn -> "WEEKLY"
                monthlyBtn -> "MONTHLY"
                else -> "DAILY"
            }
        }



// 클릭 리스너 설정
        dailyBtn.setOnClickListener { selectButton(dailyBtn) }
        weeklyBtn.setOnClickListener { selectButton(weeklyBtn) }
        monthlyBtn.setOnClickListener { selectButton(monthlyBtn) }

        nextBtn.setOnClickListener {
            val nextfragment = PushAlertFragment()
            val bundle = Bundle().apply {
                putString("title", title)
                putString("oneDose", oneDose)
                putString("goalId", goalId.toString())
                putString("authType", authType)
                putString("hour", hour)
                putString("minute", minute)
                putString("second", second)
                putString("duration", duration)
                putString("frequency", frequency)
            }
            nextfragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, nextfragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }


}