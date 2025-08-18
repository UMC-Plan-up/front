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
    private var isPlanSelected = false

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

        binding.nextButton.isEnabled = false
        binding.frequencyErrorText.visibility = View.GONE

        // SubscriptionPlanFragment에서 직접 넘어온 경우
        val isUnlockedFromSubscription = arguments?.getBoolean("IS_UNLOCKED_FROM_SUBSCRIPTION", false) ?: false
        if (isUnlockedFromSubscription) {
            binding.goalContainer.visibility = View.GONE
        }

        // GoalActivity를 통해 구독하고 돌아온 경우
        isPlanSelected = arguments?.getBoolean("PLAN_SELECTED", false) ?: false
        if (isPlanSelected) {
            binding.goalContainer.visibility = View.GONE
        }

        val isUnlocked = isUnlockedFromSubscription || isPlanSelected

        if (!isUnlocked) {
            binding.goalContainer.setOnClickListener {
                (activity as? GoalActivity)?.startSubscriptionActivity()
            }
        }

        setupBackButton()
        setupPeriodButtons()
        setupFrequencyInput()
        setupEndOptionButtons()
        setupNextButton()
        setupDirectSetSection()

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
            }
        }
        binding.dropdownYearIv.setOnClickListener { binding.dropdownContainer1.performClick() }

        binding.dropdownContainer2.setOnClickListener {
            showDropdown(months, binding.dropdownContainer2, binding.challengeMonthTv) {
                selectedMonth = it
            }
        }
        binding.dropdownMonthIv.setOnClickListener { binding.dropdownContainer2.performClick() }

        binding.dropdownContainer3.setOnClickListener {
            showDropdown(days, binding.dropdownContainer3, binding.challengeDayTv) {
                selectedDay = it
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
                if (selectedEndButton == button) {
                    // 이미 선택된 버튼을 다시 클릭한 경우 선택 해제
                    resetButtonStyle(button)
                    selectedEndButton = null

                    binding.dropdownContainer1.visibility = View.GONE
                    binding.dropdownContainer2.visibility = View.GONE
                    binding.dropdownContainer3.visibility = View.GONE

                } else {
                    selectedEndButton?.let { resetButtonStyle(it) }

                    selectButtonStyle(button)
                    selectedEndButton = button

                    // '직접 설정' 버튼 선택 시 드롭다운 표시
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
        val isReady = isPeriodSelected && isFrequencyValid

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
        val period = selectedPeriodButton?.text?.toString().orEmpty()
        val frequency = binding.frequencyInputEditText.text.toString().toIntOrNull() ?: 0

        val goalActivity = activity as? GoalActivity
        if (goalActivity != null) {
            goalActivity.period = period
            goalActivity.frequency = frequency

            // 다음 프래그먼트로 이동
            val participantFragment = ParticipantLimitFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalActivity.goalOwnerName)
                    putString("goalType", goalActivity.goalType)
                    putString("goalCategory", goalActivity.goalCategory)
                    putString("goalName", goalActivity.goalName)
                    putString("goalAmount", goalActivity.goalAmount)
                    putString("verificationType", goalActivity.verificationType)
                    putString("period", goalActivity.period)
                    putInt("frequency", goalActivity.frequency)
                }
            }

            goalActivity.navigateToFragment(participantFragment)
            return
        }

        val participantFragment = ParticipantLimitFragment().apply {
            arguments = Bundle().apply {
                putString("goalOwnerName", goalOwnerName)
                putString("verificationType", selectedMethod.orEmpty())
                putString("period", period)
                putInt("frequency", frequency)
            }
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

    fun updateLockStatus(isUnlocked: Boolean) {
        if (isUnlocked) {
            binding.goalContainer.visibility = View.GONE
        } else {
            binding.goalContainer.visibility = View.VISIBLE
        }
    }
}
