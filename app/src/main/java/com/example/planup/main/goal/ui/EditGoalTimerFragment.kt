package com.example.planup.main.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import androidx.appcompat.widget.AppCompatButton

class EditGoalTimerFragment : Fragment() {

    private lateinit var hourEt: EditText
    private lateinit var minuteEt: EditText
    private lateinit var secondEt: EditText
    private lateinit var nextBtn: AppCompatButton
    private lateinit var warningTv: TextView

    private lateinit var pickerLayout: LinearLayout
    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var secondPicker: NumberPicker

    private var selectedField: EditText? = null

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

        hourEt = view.findViewById(R.id.edit_timer_hour_et)
        minuteEt = view.findViewById(R.id.edit_timer_minute_et)
        secondEt = view.findViewById(R.id.edit_timer_second_et)
        nextBtn = view.findViewById(R.id.edit_timer_next_btn)
        warningTv = view.findViewById(R.id.edit_timer_warning_tv)

        pickerLayout = view.findViewById(R.id.edit_timer_picker_layout)
        hourPicker = view.findViewById(R.id.edit_timer_hour_picker)
        minutePicker = view.findViewById(R.id.edit_timer_minute_picker)
        secondPicker = view.findViewById(R.id.edit_timer_second_picker)

        // Picker 범위 설정
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        secondPicker.minValue = 0
        secondPicker.maxValue = 59

        // EditText 클릭 시 Picker 열기
        hourEt.setOnClickListener { showPicker(hourPicker, hourEt) }
        minuteEt.setOnClickListener { showPicker(minutePicker, minuteEt) }
        secondEt.setOnClickListener { showPicker(secondPicker, secondEt) }

        // Picker 변경 시 EditText 업데이트
        hourPicker.setOnValueChangedListener { _, _, newVal -> onPickerValueChanged(newVal) }
        minutePicker.setOnValueChangedListener { _, _, newVal -> onPickerValueChanged(newVal) }
        secondPicker.setOnValueChangedListener { _, _, newVal -> onPickerValueChanged(newVal) }

        nextBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("hour", hourEt.text.toString())
                putString("minute", minuteEt.text.toString())
                putString("second", secondEt.text.toString())
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

        return view
    }

    private fun showPicker(picker: NumberPicker, field: EditText) {
        pickerLayout.visibility = View.VISIBLE
        selectedField = field

        // 모든 picker 숨기고 선택한 것만 보이기
        hourPicker.visibility = View.GONE
        minutePicker.visibility = View.GONE
        secondPicker.visibility = View.GONE

        picker.visibility = View.VISIBLE

        val currentVal = field.text.toString().toIntOrNull() ?: 0
        picker.value = currentVal
    }

    private fun onPickerValueChanged(value: Int) {
        selectedField?.setText(value.toString())
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        val hour = hourEt.text.toString().toIntOrNull() ?: 0
        val minute = minuteEt.text.toString().toIntOrNull() ?: 0
        val second = secondEt.text.toString().toIntOrNull() ?: 0

        val isFilled = hourEt.text.isNotBlank() &&
                minuteEt.text.isNotBlank() &&
                secondEt.text.isNotBlank()

        val totalSeconds = hour * 3600 + minute * 60 + second
        val isTimeEnough = totalSeconds >= 30

        nextBtn.isEnabled = isFilled && isTimeEnough

        if (isFilled && isTimeEnough) {
            nextBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_300))
            warningTv.visibility = View.GONE
        } else {
            nextBtn.setBackgroundResource(R.drawable.btn_next_background_gray)
            warningTv.visibility = View.VISIBLE
        }
    }
}
