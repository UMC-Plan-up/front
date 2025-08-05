package com.example.planup.main.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.planup.R
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton

class EditGoalTimerFragment : Fragment() {

    private lateinit var hourEt: EditText
    private lateinit var minuteEt: EditText
    private lateinit var secondEt: EditText
    private lateinit var nextBtn: AppCompatButton
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

        nextBtn.isEnabled = false
        updateNextButtonState()

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateNextButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        hourEt.addTextChangedListener(watcher)
        minuteEt.addTextChangedListener(watcher)
        secondEt.addTextChangedListener(watcher)

        nextBtn.setOnClickListener {
            val hour = hourEt.text.toString()
            val minute = minuteEt.text.toString()
            val second = secondEt.text.toString()

            val bundle = Bundle().apply {
                putString("hour", hour)
                putString("minute", minute)
                putString("second", second)
                putString("goalId", goalId.toString())
                putString("title", title)
                putString("oneDose", oneDose)
                putString("authType","camera")
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

    private fun updateNextButtonState() {
        val isFilled = hourEt.text.isNotBlank() &&
                minuteEt.text.isNotBlank() &&
                secondEt.text.isNotBlank()

        nextBtn.isEnabled = isFilled
        nextBtn.setBackgroundResource(
            if (isFilled) R.color.blue_300 else R.drawable.btn_next_background_gray
        )
    }

}