package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalTimerBinding

class EditGoalTimerFragment : Fragment() {
    private lateinit var binding: FragmentEditGoalTimerBinding

    private lateinit var hourSpinner: Spinner
    private lateinit var minuteSpinner: Spinner
    private lateinit var secondSpinner: Spinner
    private lateinit var nextBtn: AppCompatButton
    private lateinit var warningTv: TextView

    private var goalId: Long = -1L
    private var title: String = ""
    private var oneDose: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getLong("goalId") ?: -1L
        title = arguments?.getString("title") ?: ""
        oneDose = arguments?.getString("oneDose") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_goal_timer, container, false)

        hourSpinner = binding.editTimerHourSpinner
        minuteSpinner = binding.editTimerMinuteSpinner
        secondSpinner = binding.editTimerSecondSpinner
        nextBtn = binding.editTimerNextBtn
        warningTv = binding.editTimerWarningTv

        // Spinner 항목 리스트 생성
        val hourList = (0..23).map { it.toString().padStart(2, '0') }
        val minuteSecondList = (0..59).map { it.toString().padStart(2, '0') }
        val secondList = (0..59).map { it.toString().padStart(2, '0') }

        // Spinner 어댑터 설정
        val hourAdapter = ArrayAdapter(requireContext(), R.layout.item_edit_goal_spinner_text, hourList)
        hourAdapter.setDropDownViewResource(R.layout.item_edit_goal_spinner_dropdown_text)

        val minuteAdapter = ArrayAdapter(requireContext(), R.layout.item_edit_goal_spinner_text, minuteSecondList)
        minuteAdapter.setDropDownViewResource(R.layout.item_edit_goal_spinner_dropdown_text)

        val secondAdapter = ArrayAdapter(requireContext(), R.layout.item_edit_goal_spinner_text, secondList)
        secondAdapter.setDropDownViewResource(R.layout.item_edit_goal_spinner_dropdown_text)

        hourSpinner.adapter = hourAdapter
        minuteSpinner.adapter = minuteAdapter
        secondSpinner.adapter = secondAdapter

        // 리스너 등록
        hourSpinner.onItemSelectedListener = spinnerListener
        minuteSpinner.onItemSelectedListener = spinnerListener
        secondSpinner.onItemSelectedListener = spinnerListener

        nextBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("hour", hourSpinner.selectedItem.toString())
                putString("minute", minuteSpinner.selectedItem.toString())
                putString("second", secondSpinner.selectedItem.toString())
                putString("goalId", goalId.toString())
                putString("title", title)
                putString("oneDose", oneDose)
                putString("authType", "camera")
            }

            val nextFragment = EditGoalDetailFragment().apply {
                arguments = bundle
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()
        }

        updateNextButtonState()

        return view
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updateNextButtonState()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun updateNextButtonState() {
        val hour = hourSpinner.selectedItem.toString().toIntOrNull() ?: 0
        val minute = minuteSpinner.selectedItem.toString().toIntOrNull() ?: 0
        val second = secondSpinner.selectedItem.toString().toIntOrNull() ?: 0

        val totalSeconds = hour * 3600 + minute * 60 + second
        val isTimeEnough = totalSeconds >= 30

        nextBtn.isEnabled = isTimeEnough

        if (isTimeEnough) {
            nextBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_300))
            warningTv.visibility = View.GONE
        } else {
            nextBtn.setBackgroundResource(R.drawable.btn_next_background_gray)
            warningTv.visibility = View.VISIBLE
        }
    }
}
