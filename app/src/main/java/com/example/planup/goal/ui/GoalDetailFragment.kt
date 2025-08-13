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
import com.example.planup.databinding.FragmentGoalDetailBinding // 뷰 바인딩 클래스 import
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

        // 닉네임 반영
        binding.friendGoalTitle.text = getString(R.string.goal_friend_detail, goalOwnerName)

        val shouldHideGoalContainer = arguments?.getBoolean("HIDE_GOAL_CONTAINER", false) ?: false
        if (shouldHideGoalContainer) {
            binding.goalContainer.visibility = View.GONE
        }

        binding.nextButton.isEnabled = false
        binding.frequencyErrorText.visibility = View.GONE

        setupBackButton()
        setupPeriodButtons()
        setupFrequencyInput()
        setupNextButton()
        setupDirectSetSection()

        binding.root.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN &&
                binding.frequencyInputEditText.isFocused
            ) {
                binding.frequencyInputEditText.clearFocus()
                hideKeyboard()
            }
            binding.root.performClick()
            false
        }
    }

    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
    private fun setupBackButton() {
        binding.backIcon.setOnClickListener {
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

    /* 직접 설정하기 */
    private fun setupDirectSetSection() {
        binding.directSetButton.setOnClickListener {
            binding.dropdownContainer1.visibility = View.VISIBLE
            binding.dropdownContainer2.visibility = View.VISIBLE
            binding.dropdownContainer3.visibility = View.VISIBLE
        }

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

    // 연도
    private fun buildYearItems(): ArrayList<String> {
        return (2025..2035).map { it.toString() }.toCollection(ArrayList())
    }

    // 월
    private fun buildMonthItems(): ArrayList<String> {
        return (1..12).map { String.format("%02d", it) }.toCollection(ArrayList())
    }

    // 일
    private fun buildDayItems(): ArrayList<String> {
        return (1..31).map { String.format("%02d", it) }.toCollection(ArrayList())
    }

    /* 드롭다운 표시 */
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
        popupWindow.width = anchor.width

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

    /* 다음 버튼 활성화 조건 */
    private fun updateNextButtonState() {
        val isPeriodSelected = selectedPeriodButton != null
        val isDateOk =
            if (binding.dropdownContainer1.visibility == View.VISIBLE) {
                selectedYear != null && selectedMonth != null && selectedDay != null
            } else true

        binding.nextButton.isEnabled = isPeriodSelected && isFrequencyValid && isDateOk

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

    /* 다음 버튼 → ParticipantLimitFragment 이동 */
    private fun setupNextButton() {
        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {
                val activity = requireActivity() as GoalActivity

                activity.verificationType = selectedMethod ?: ""
                activity.period = selectedPeriodButton?.text?.toString() ?: ""
                activity.frequency =
                    binding.frequencyInputEditText.text.toString().toIntOrNull() ?: 0

                val participantFragment = ParticipantLimitFragment().apply {
                    arguments = Bundle().apply {
                        putString("goalOwnerName", goalOwnerName)
                    }
                }
                activity.navigateToFragment(participantFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
