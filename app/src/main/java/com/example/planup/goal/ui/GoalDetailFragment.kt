package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalDetailBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.TimerRVAdapter
import com.example.planup.main.goal.ui.SubscriptionPlanFragment

class GoalDetailFragment : Fragment() {

    private var _binding: FragmentGoalDetailBinding? = null
    private val binding get() = _binding!!

    private var selectedPeriodButton: AppCompatButton? = null
    private var isFrequencyValid = false
    private var selectedMethod: String? = null
    private var goalOwnerName: String? = null

    private var selectedYear: String? = null
    private var selectedMonth: String? = null
    private var selectedDay: String? = null

    private var selectedEndButton: AppCompatButton? = null

    // 구독 플랜 선택 여부 (초기값은 false)
    private var isSubscribed: Boolean = false

    // 상태 저장을 위한 상수 정의
    private val KEY_IS_SUBSCRIBED = "isSubscribed"
    private val KEY_SELECTED_PERIOD_BUTTON = "selectedPeriodButtonId"
    private val KEY_FREQUENCY_INPUT = "frequencyInput"
    private val KEY_SELECTED_END_BUTTON = "selectedEndButtonId"
    private val KEY_SELECTED_YEAR = "selectedYear"
    private val KEY_SELECTED_MONTH = "selectedMonth"
    private val KEY_SELECTED_DAY = "selectedDay"
    private val KEY_DROPDOWN_VISIBILITY = "dropdownVisibility"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedMethod = arguments?.getString("SELECTED_METHOD")
        goalOwnerName = arguments?.getString("goalOwnerName")
            ?: (activity as? GoalActivity)?.goalOwnerName
                    ?: "사용자"

        binding.friendGoalTitle.text = getString(R.string.goal_friend_detail, goalOwnerName)

        // Subscription에서 돌아오면 자물쇠를 풀린 상태로 표시
        val shouldHideGoalContainer = arguments?.getBoolean("HIDE_GOAL_CONTAINER", false) ?: false
        if (shouldHideGoalContainer) {
            binding.goalContainer.visibility = View.GONE
            isSubscribed = true
        } else {
            binding.goalContainer.visibility = View.VISIBLE
            isSubscribed = false
        }

        // Bundle에서 상태 복원
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        // SubscriptionFragment에서 돌아올 때 결과를 받는 리스너
        setFragmentResultListener()

        binding.nextButton.isEnabled = false
        binding.frequencyErrorText.visibility = View.GONE

        setupBackButton()
        setupPeriodButtons()
        setupFrequencyInput()
        setupEndOptionButtons()
        setupNextButton()
        setupDirectSetSection()
        setupGoalContainer() // 자물쇠 클릭 리스너 추가

        // 키보드 숨기기 로직
        binding.root.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                val focused = requireActivity().currentFocus
                if (focused != null) {
                    focused.clearFocus()
                    hideKeyboard()
                }
            }
            v.performClick()
            false
        }
    }

    // Fragment Result API 리스너 설정
    private fun setFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener("subscription_result_key", viewLifecycleOwner) { key, bundle ->
            val isPlanSelected = bundle.getBoolean("is_plan_selected", false)
            if (isPlanSelected) {
                isSubscribed = true
                binding.goalContainer.visibility = View.GONE
                updateNextButtonState()
            }
        }
    }

    // 자물쇠 클릭 리스너 설정
    private fun setupGoalContainer() {
        binding.goalContainer.setOnClickListener {
            val subscriptionFragment = SubscriptionPlanFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("IS_FROM_GOAL_DETAIL", true)
                    putString("goalOwnerName", goalOwnerName)
                }
            }

            (requireActivity() as? GoalActivity)?.navigateToFragment(subscriptionFragment)
                ?: parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, subscriptionFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    // 상태 복원 함수
    private fun restoreState(savedInstanceState: Bundle) {
        // isSubscribed 상태 복원
        isSubscribed = savedInstanceState.getBoolean(KEY_IS_SUBSCRIBED, false)
        if (isSubscribed) {
            binding.goalContainer.visibility = View.GONE
        } else {
            binding.goalContainer.visibility = View.VISIBLE
        }

        // Period Button 상태 복원
        val selectedPeriodButtonId = savedInstanceState.getInt(KEY_SELECTED_PERIOD_BUTTON, -1)
        if (selectedPeriodButtonId != -1) {
            val button = view?.findViewById<AppCompatButton>(selectedPeriodButtonId)
            if (button != null) {
                resetAllPeriodButtons()
                selectButtonStyle(button)
                selectedPeriodButton = button
            }
        }

        // Frequency Input 상태 복원
        val frequencyInput = savedInstanceState.getString(KEY_FREQUENCY_INPUT)
        if (frequencyInput != null) {
            binding.frequencyInputEditText.setText(frequencyInput)
            val value = frequencyInput.toIntOrNull() ?: 0
            isFrequencyValid = value >= 1
            if (!isFrequencyValid) {
                binding.frequencyErrorText.visibility = View.VISIBLE
            }
        }

        // End Option Button 상태 복원
        val selectedEndButtonId = savedInstanceState.getInt(KEY_SELECTED_END_BUTTON, -1)
        if (selectedEndButtonId != -1) {
            val button = view?.findViewById<AppCompatButton>(selectedEndButtonId)
            if (button != null) {
                resetAllEndButtons()
                selectButtonStyle(button)
                selectedEndButton = button
            }
        }

        // 직접 설정 드롭다운 상태 복원
        val dropdownVisibility = savedInstanceState.getInt(KEY_DROPDOWN_VISIBILITY, View.GONE)
        binding.dropdownContainer1.visibility = dropdownVisibility
        binding.dropdownContainer2.visibility = dropdownVisibility
        binding.dropdownContainer3.visibility = dropdownVisibility

        val year = savedInstanceState.getString(KEY_SELECTED_YEAR)
        val month = savedInstanceState.getString(KEY_SELECTED_MONTH)
        val day = savedInstanceState.getString(KEY_SELECTED_DAY)

        if (year != null) {
            selectedYear = year
            binding.challengeYearTv.text = year
        }
        if (month != null) {
            selectedMonth = month
            binding.challengeMonthTv.text = month
        }
        if (day != null) {
            selectedDay = day
            binding.challengeDayTv.text = day
        }

        updateNextButtonState()
    }

    // 상태 저장 함수
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_SUBSCRIBED, isSubscribed)
        selectedPeriodButton?.let { outState.putInt(KEY_SELECTED_PERIOD_BUTTON, it.id) }
        outState.putString(KEY_FREQUENCY_INPUT, binding.frequencyInputEditText.text.toString())
        selectedEndButton?.let { outState.putInt(KEY_SELECTED_END_BUTTON, it.id) }
        selectedYear?.let { outState.putString(KEY_SELECTED_YEAR, it) }
        selectedMonth?.let { outState.putString(KEY_SELECTED_MONTH, it) }
        selectedDay?.let { outState.putString(KEY_SELECTED_DAY, it) }
        outState.putInt(KEY_DROPDOWN_VISIBILITY, binding.dropdownContainer1.visibility)
    }

    private fun setupBackButton() {
        binding.backIcon.setOnClickListener {
            when (selectedMethod) {
                "TIMER" -> {
                    val timerFragment = TimerSettingFragment().apply {
                        arguments = Bundle().apply {
                            putString("goalOwnerName", goalOwnerName)
                        }
                    }
                    (activity as? GoalActivity)?.navigateToFragment(timerFragment)
                        ?: requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                "PICTURE" -> {
                    val certFragment = CertificationMethodFragment().apply {
                        arguments = Bundle().apply {
                            putString("goalOwnerName", goalOwnerName)
                        }
                    }
                    (activity as? GoalActivity)?.navigateToFragment(certFragment)
                        ?: requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                else -> requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupPeriodButtons() {
        val buttons = listOf(
            binding.dayOptionDailyButton,
            binding.dayOptionWeeklyButton,
            binding.dayOptionMonthlyButton
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                selectedPeriodButton?.let { resetButtonStyle(it) }
                selectButtonStyle(button)
                selectedPeriodButton = button
                updateNextButtonState()
            }
        }
    }

    private fun selectButtonStyle(button: AppCompatButton) {
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_200))
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_selected_blue)
    }

    private fun resetButtonStyle(button: AppCompatButton) {
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_300))
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_no)
    }

    private fun resetAllPeriodButtons() {
        val buttons = listOf(
            binding.dayOptionDailyButton,
            binding.dayOptionWeeklyButton,
            binding.dayOptionMonthlyButton
        )
        buttons.forEach { resetButtonStyle(it) }
    }

    private fun resetAllEndButtons() {
        val buttons = listOf(
            binding.endOption1WeekButton,
            binding.endOption1MonthButton,
            binding.endOption3MonthButton,
            binding.endOption6MonthButton,
            binding.endOption1YearButton,
            binding.directSetButton
        )
        buttons.forEach { resetButtonStyle(it) }
    }

    private fun setupFrequencyInput() {
        binding.frequencyInputEditText.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]*"))) source else ""
        })

        binding.frequencyInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull() ?: 0
                if (value < 1) {
                    binding.frequencyErrorText.visibility = View.VISIBLE
                    isFrequencyValid = false
                } else {
                    binding.frequencyErrorText.visibility = View.GONE
                    isFrequencyValid = true
                }
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupDirectSetSection() {
        val years = buildYearItems()
        val months = buildMonthItems()
        val days = buildDayItems()

        binding.dropdownContainer1.setOnClickListener {
            showDropdown(years, binding.dropdownContainer1, binding.challengeYearTv) {
                selectedYear = it
                updateNextButtonState() // 선택 시 바로 업데이트
            }
        }
        binding.dropdownYearIv.setOnClickListener { binding.dropdownContainer1.performClick() }

        binding.dropdownContainer2.setOnClickListener {
            showDropdown(months, binding.dropdownContainer2, binding.challengeMonthTv) {
                selectedMonth = it
                updateNextButtonState()
            }
        }
        binding.dropdownMonthIv.setOnClickListener { binding.dropdownContainer2.performClick() }

        binding.dropdownContainer3.setOnClickListener {
            showDropdown(days, binding.dropdownContainer3, binding.challengeDayTv) {
                selectedDay = it
                updateNextButtonState()
            }
        }
        binding.dropdownDayIv.setOnClickListener { binding.dropdownContainer3.performClick() }
    }

    private fun setupEndOptionButtons() {
        val buttons = listOf(
            binding.endOption1WeekButton,
            binding.endOption1MonthButton,
            binding.endOption3MonthButton,
            binding.endOption6MonthButton,
            binding.endOption1YearButton,
            binding.directSetButton
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                selectedEndButton?.let { resetButtonStyle(it) }
                selectButtonStyle(button)
                selectedEndButton = button

                if (button == binding.directSetButton) {
                    binding.dropdownContainer1.visibility = View.VISIBLE
                    binding.dropdownContainer2.visibility = View.VISIBLE
                    binding.dropdownContainer3.visibility = View.VISIBLE
                } else {
                    binding.dropdownContainer1.visibility = View.GONE
                    binding.dropdownContainer2.visibility = View.GONE
                    binding.dropdownContainer3.visibility = View.GONE
                    selectedYear = null
                    selectedMonth = null
                    selectedDay = null
                    binding.challengeYearTv.text = getString(R.string.year)
                    binding.challengeMonthTv.text = getString(R.string.month)
                    binding.challengeDayTv.text = getString(R.string.day)
                }

                updateNextButtonState()
            }
        }
    }

    private fun buildYearItems(): ArrayList<String> {
        return (2025..2035).map { it.toString() }.toCollection(ArrayList())
    }

    private fun buildMonthItems(): ArrayList<String> {
        return (1..12).map { String.format("%02d", it) }.toCollection(ArrayList())
    }

    private fun buildDayItems(): ArrayList<String> {
        return (1..31).map { String.format("%02d", it) }.toCollection(ArrayList())
    }

    private fun showDropdown(
        items: ArrayList<String>,
        anchor: View,
        label: TextView,
        onPicked: (String) -> Unit
    ) {
        val popupView = layoutInflater.inflate(R.layout.item_recycler_dropdown_time, null)
        val popupWindow = android.widget.PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(
            ContextCompat.getColor(requireContext(), R.color.transparent).toDrawable()
        )
        popupWindow.elevation = 8f
        popupWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT

        popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).apply {
            val adapter = TimerRVAdapter(items)
            this.adapter = adapter
            adapter.setDropdownListener(object : TimerRVAdapter.DropdownListener {
                override fun setTime(position: Int) {
                    val selectedText = items[position]
                    label.text = selectedText
                    onPicked(selectedText)
                    updateNextButtonState()
                    popupWindow.dismiss()
                }
            })
        }

        popupWindow.showAsDropDown(anchor, 0, 0)
    }

    private fun updateNextButtonState() {
        val isPeriodSelected = selectedPeriodButton != null
        val isFrequencyValid = binding.frequencyInputEditText.text.toString().toIntOrNull() ?: 0 >= 1

        val isDirectSetSelected = selectedEndButton == binding.directSetButton
        val isDirectDateValid = if (isDirectSetSelected) {
            selectedYear != null && selectedMonth != null && selectedDay != null
        } else {
            selectedEndButton != null
        }

        // 모든 조건이 충족되어야 다음 버튼 활성화
        val isReady = isSubscribed && isPeriodSelected && isFrequencyValid && isDirectDateValid

        binding.nextButton.isEnabled = isReady

        if (binding.nextButton.isEnabled) {
            binding.nextButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        } else {
            binding.nextButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background_gray)
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun setupNextButton() {
        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {
                goToParticipantAlways()
            }
        }
    }

    private fun goToParticipantAlways() {
        val verificationType = selectedMethod.orEmpty()
        val period = selectedPeriodButton?.text?.toString().orEmpty()
        val frequency = binding.frequencyInputEditText.text.toString().toIntOrNull() ?: 0

        val participantFragment = ParticipantLimitFragment().apply {
            arguments = Bundle().apply {
                putString("goalOwnerName", goalOwnerName)
                putString("verificationType", verificationType)
                putString("period", period)
                putInt("frequency", frequency)
            }
        }

        val goalActivity = activity as? GoalActivity
        if (goalActivity != null) {
            goalActivity.verificationType = verificationType
            goalActivity.period = period
            goalActivity.frequency = frequency
            goalActivity.navigateToFragment(participantFragment)
            return
        }

        val containerId = (view?.parent as? ViewGroup)?.id
        if (containerId != null && containerId != View.NO_ID) {
            parentFragmentManager.beginTransaction()
                .replace(containerId, participantFragment)
                .addToBackStack(null)
                .commit()
            return
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, participantFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}