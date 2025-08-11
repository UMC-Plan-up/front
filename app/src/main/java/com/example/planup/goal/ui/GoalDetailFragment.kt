package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity

class GoalDetailFragment : Fragment(R.layout.fragment_goal_detail) {

    private lateinit var backIcon: View
    private lateinit var nextButton: AppCompatButton
    private lateinit var dailyButton: AppCompatButton
    private lateinit var weeklyButton: AppCompatButton
    private lateinit var monthlyButton: AppCompatButton
    private var selectedPeriodButton: AppCompatButton? = null
    private lateinit var frequencyEditText: EditText
    private lateinit var frequencyErrorText: TextView
    private var isFrequencyValid = false
    private var selectedMethod: String? = null
    private var goalOwnerName: String? = null
    private lateinit var friendGoalTitle: TextView
    private lateinit var friendGoalDescriptionText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedMethod = arguments?.getString("SELECTED_METHOD")
        goalOwnerName = arguments?.getString("goalOwnerName")
            ?: throw IllegalStateException("GoalDetailFragment must receive goalOwnerName!")

        initViews(view)

        friendGoalTitle.text = getString(R.string.goal_friend_detail, goalOwnerName)

        setupBackButton()
        setupPeriodButtons()
        setupFrequencyInput()
        setupNextButton()

        view.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN &&
                frequencyEditText.isFocused
            ) {
                frequencyEditText.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }
    }


    private fun initViews(view: View) {
        backIcon = view.findViewById(R.id.backIcon)
        nextButton = view.findViewById(R.id.nextButton)

        // 기준 기간 버튼
        dailyButton = view.findViewById(R.id.dayOptionDailyButton)
        weeklyButton = view.findViewById(R.id.dayOptionWeeklyButton)
        monthlyButton = view.findViewById(R.id.dayOptionMonthlyButton)

        // 빈도 입력
        frequencyEditText = view.findViewById(R.id.frequencyInputEditText)
        frequencyErrorText = view.findViewById(R.id.frequencyErrorText)


        friendGoalTitle = view.findViewById(R.id.friendGoalTitle)
        friendGoalDescriptionText = view.findViewById(R.id.friendGoalDescriptionText)

        nextButton.isEnabled = false
        frequencyErrorText.visibility = View.GONE
    }

    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
    private fun setupBackButton() {
        backIcon.setOnClickListener {
            when (selectedMethod) {
                "TIMER" -> {
                    val timerFragment = TimerSettingFragment().apply {
                        arguments = Bundle().apply {
                            putString("goalOwnerName", goalOwnerName)
                        }
                    }

                    (requireActivity() as GoalActivity)
                        .navigateToFragment(timerFragment)
                }
                "PICTURE" -> {
                    val certFragment = CertificationMethodFragment().apply {
                        arguments = Bundle().apply {
                            putString("goalOwnerName", goalOwnerName)
                        }
                    }
                    (requireActivity() as GoalActivity)
                        .navigateToFragment(certFragment)
                }
                else -> requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    /* 기준 기간 버튼 선택 */
    private fun setupPeriodButtons() {
        val buttons = listOf(dailyButton, weeklyButton, monthlyButton)

        buttons.forEach { button ->
            button.setOnClickListener {
                // 이전 선택 버튼 스타일 초기화
                selectedPeriodButton?.let { resetButtonStyle(it) }

                // 현재 클릭한 버튼 스타일 선택
                selectButtonStyle(button)
                selectedPeriodButton = button

                updateNextButtonState()
            }
        }
    }

    /* 선택된 버튼 */
    private fun selectButtonStyle(button: AppCompatButton) {
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_200))
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_selected_blue)
    }

    /* 선택 해제된 버튼 */
    private fun resetButtonStyle(button: AppCompatButton) {
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_300))
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_no)
    }

    /* 빈도 입력 */
    private fun setupFrequencyInput() {
        // 숫자만 입력 가능
        frequencyEditText.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]*"))) source else ""
        })

        frequencyEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull() ?: 0
                if (value < 1) {
                    // 1 미만 → 오류 메시지 표시
                    frequencyErrorText.visibility = View.VISIBLE
                    isFrequencyValid = false
                } else {
                    // 정상 입력 → 오류 숨김
                    frequencyErrorText.visibility = View.GONE
                    isFrequencyValid = true
                }
                updateNextButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /* 다음 버튼 활성화 조건 */
    private fun updateNextButtonState() {
        val isPeriodSelected = selectedPeriodButton != null
        nextButton.isEnabled = isPeriodSelected && isFrequencyValid

        if (nextButton.isEnabled) {
            nextButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        } else {
            nextButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background_gray)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /* 다음 버튼 → ParticipantLimitFragment 이동 */
    private fun setupNextButton() {
        nextButton.setOnClickListener {
            if (nextButton.isEnabled) {
                val activity = requireActivity() as GoalActivity

                activity.verificationType = selectedMethod ?: ""
                activity.period = selectedPeriodButton?.text?.toString() ?: ""
                activity.frequency = frequencyEditText.text.toString().toIntOrNull() ?: 0

                val participantFragment = ParticipantLimitFragment().apply {
                    arguments = Bundle().apply {
                        putString("goalOwnerName", goalOwnerName)
                    }
                }
                activity.navigateToFragment(participantFragment)
            }
        }
    }
}
