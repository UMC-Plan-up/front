package com.example.planup.main.goal.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalCntBinding
import android.widget.TextView
import android.text.TextWatcher
import kotlin.text.toInt

class EditGoalCntFragment : Fragment() {

    private lateinit var binding: FragmentEditGoalCntBinding
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
    ): View {
        binding = FragmentEditGoalCntBinding.inflate(inflater, container, false)

        // EditText 기본값 세팅
        binding.editText.setText(limitFriendCount.toString())

        // 빨간 글씨 안내 TextView
        val warningText = binding.root.findViewById<TextView>(R.id.edit_goal_cnt_warning_tv)
        // textView_warning는 XML에서 빨간 글씨 TextView id로 변경해주세요

        // TextWatcher로 입력값 감시
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().toIntOrNull() ?: 0
                warningText.visibility = if (input < limitFriendCount) View.VISIBLE else View.GONE
            }
        })

        // 다음 버튼 클릭
        binding.editCountNextBtn.setOnClickListener {
            val inputCount = binding.editText.text.toString().toIntOrNull() ?: limitFriendCount

            val bundle = Bundle().apply {
                putInt("goalId", goalId)
                putString("goalName", goalName)
                putString("goalAmount", goalAmount)
                putString("goalCategory", goalCategory)
                putString("goalType", goalType)
                putInt("oneDose", oneDose)
                putInt("frequency", frequency.toInt())
                putString("period", period)
                putString("endDate", endDate)
                putString("verificationType", verificationType)
                putInt("limitFriendCount", inputCount)
                putInt("goalTime", goalTime)
            }

            val nextFragment = PushAlertFragment() // 실제 다음 Fragment로 교체
            nextFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, nextFragment) // 실제 container id로 변경
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }
}
