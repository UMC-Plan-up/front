package com.planup.planup.goal.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.planup.planup.R
import com.planup.planup.databinding.FragmentGoalDetailBinding
import com.planup.planup.goal.GoalActivity
import com.planup.planup.goal.adapter.TimerRVAdapter
import com.planup.planup.goal.util.backStackTrueGoalNav
import com.planup.planup.goal.util.daysFromToday
import com.planup.planup.goal.util.endDateFromToday
import com.planup.planup.goal.util.equil
import com.planup.planup.goal.util.logGoalActivityData
import com.planup.planup.goal.util.setInsets
import com.planup.planup.goal.util.titleFormat
import com.planup.planup.main.goal.viewmodel.GoalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    private val viewModel: GoalViewModel by activityViewModels()

    private lateinit var periodButtons: List<AppCompatButton>
    private lateinit var endOptionButtons: List<AppCompatButton>
    private lateinit var _endOptionButtons: List<AppCompatButton>

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
        setInsets(binding.root)
        periodButtons = listOf(
            binding.dayOptionDailyButton,
            binding.dayOptionWeeklyButton,
            binding.dayOptionMonthlyButton
        )

        _endOptionButtons = listOf(
            binding.endOption1WeekButton,
            binding.endOption1MonthButton,
            binding.endOption3MonthButton,
            binding.endOption6MonthButton,
            binding.endOption1YearButton,
            binding.directSetButton
        )

//        binding.friendGoalTitle.text = getString(R.string.goal_friend_detail, goalOwnerName)
        binding.frequencyErrorText.visibility = View.GONE

        // SubscriptionPlanFragment에서 직접 넘어온 경우
//        val isUnlockedFromSubscription = arguments?.getBoolean("IS_UNLOCKED_FROM_SUBSCRIPTION", false) ?: false
//        if (isUnlockedFromSubscription) {
//            binding.goalContainer.visibility = View.GONE
//        }

        // GoalActivity를 통해 유료 결제하고 돌아온 경우
//        isPlanSelected = arguments?.getBoolean("PLAN_SELECTED", false) ?: false
//        if (isPlanSelected) {
//            binding.goalContainer.visibility = View.GONE
//        }

//        val isUnlocked = isUnlockedFromSubscription || isPlanSelected
//
//        if (!isUnlocked) {
//            binding.goalContainer.setOnClickListener {
//                (activity as? GoalActivity)?.startSubscriptionActivity()
//            }
//        }

        setupBackButton()
        setupPeriodButtons()
        setupFrequencyInput()
        setupEndOptionButtons()
        setupNextButton()
        setupDirectSetSection()
        setupKeyboardHiding()

        setEdit()
    }

    private fun hideKeyboardAndClearFocus() {
        _binding?.let { binding ->
            val view = requireActivity().currentFocus ?: binding.root
            view.clearFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setupKeyboardHiding() {
        _binding?.let { binding ->
            binding.root.setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    hideKeyboardAndClearFocus()
                }
                false
            }
            binding.frequencyInputEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    hideKeyboardAndClearFocus()
                }
            }
        }
    }

    private fun setupBackButton() {
        binding.backIcon.setOnClickListener {
//            val previousFragment = arguments?.getString("PREVIOUS_FRAGMENT")
//
//            if (previousFragment == "ParticipantLimitFragment") {
//                requireActivity().onBackPressedDispatcher.onBackPressed()
//            } else {
//                when (selectedMethod) {
//                    "TIMER" -> {
//                        val timerFragment = TimerSettingFragment().apply {
//                            arguments = Bundle().apply {
//                                putString("goalOwnerName", goalOwnerName)
//                            }
//                        }
//                        (activity as? GoalActivity)?.navigateToFragment(timerFragment)
//                    }
//                    "PICTURE" -> {
//                        val certFragment = CertificationMethodFragment().apply {
//                            arguments = Bundle().apply {
//                                putString("goalOwnerName", goalOwnerName)
//                            }
//                        }
//                        (activity as? GoalActivity)?.navigateToFragment(certFragment)
//                    }
//                    else -> requireActivity().onBackPressedDispatcher.onBackPressed()
//                }
//            }
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupPeriodButtons() {
        periodButtons.forEach { button ->
            button.setOnClickListener {
                setPeriodListener(button)
                if(button.text == binding.dayOptionMonthlyButton.text) {
                    endOptionButtons = _endOptionButtons.drop(1)
                    binding.endOption1WeekButton.visibility = View.GONE
                }else {
                    endOptionButtons = _endOptionButtons
                    binding.endOption1WeekButton.visibility = View.VISIBLE
                }
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
                setErrorText(s.toString().toIntOrNull() ?: 0)
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
            showDropdown(years, binding.dropdownContainer1, binding.challengeYearTv) { selected ->
                selectedYear = selected
                binding.challengeYearTv.text = getString(R.string.year_unit, selected)
                updateNextButtonState()
            }
        }
        binding.dropdownYearIv.setOnClickListener { binding.dropdownContainer1.performClick() }

        binding.dropdownContainer2.setOnClickListener {
            showDropdown(months, binding.dropdownContainer2, binding.challengeMonthTv) { selected ->
                selectedMonth = selected
                binding.challengeMonthTv.text = getString(R.string.month_unit, selected)
                updateNextButtonState()
            }
        }
        binding.dropdownMonthIv.setOnClickListener { binding.dropdownContainer2.performClick() }

        binding.dropdownContainer3.setOnClickListener {
            showDropdown(days, binding.dropdownContainer3, binding.challengeDayTv) { selected ->
                selectedDay = selected
                binding.challengeDayTv.text = getString(R.string.day_unit, selected)
                updateNextButtonState()
            }
        }
        binding.dropdownDayIv.setOnClickListener { binding.dropdownContainer3.performClick() }
    }

    private fun setupEndOptionButtons() {
        val goalActivity = (requireActivity() as GoalActivity)
        val period = when(goalActivity.period){
            "DAY"-> "매일"
            "WEEK"-> "매주"
            "MONTH"-> "매달"
            else -> goalActivity.period
        }
        endOptionButtons = monthEndUi(period)
        endOptionButtons.forEach { button ->
            button.setOnClickListener {
                setEndListener(button)
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

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

    private fun showDropdown(
        items: ArrayList<String>,
        anchor: View,
        label: TextView,
        onPicked: (String) -> Unit
    ) {
        val popupView = layoutInflater.inflate(R.layout.item_recycler_dropdown_time, null)

        val fallbackWidth = when (anchor.id) {
            R.id.dropdown_container1 -> 100.dp() // 연도 박스
            R.id.dropdown_container2 ->  80.dp() // 월 박스
            R.id.dropdown_container3 ->  80.dp() // 일 박스
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }

        anchor.post {
            val exactWidth = if (anchor.width > 0) anchor.width else fallbackWidth

            val popupWindow = android.widget.PopupWindow(
                popupView,
                exactWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            ).apply {
                isOutsideTouchable = true
                setBackgroundDrawable(
                    ContextCompat.getColor(requireContext(), R.color.transparent).toDrawable()
                )
                elevation = 8f
            }

            popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).apply {
                adapter = TimerRVAdapter(items).apply {
                    setDropdownListener(object : TimerRVAdapter.DropdownListener {
                        override fun setTime(position: Int) {
                            val selectedText = items[position]
                            onPicked(selectedText)
                            popupWindow.dismiss()
                        }
                    })
                }
            }

            popupWindow.showAsDropDown(anchor, 0, 0)
        }
    }



    private fun updateNextButtonState() {
        val isPeriodSelected = selectedPeriodButton != null
        val isFrequencyInputValid = isFrequencyValid
        val isReady = isPeriodSelected && isFrequencyInputValid
        Log.d("GoalDetailFragment", "isReady: $isReady")
        binding.nextButton.isEnabled = isReady
        binding.nextButton.background =
            ContextCompat.getDrawable(
                requireContext(),
                if (isReady) R.drawable.btn_next_background else R.drawable.btn_next_background_gray
            )
    }


    // "다음" 버튼 클릭
    private fun setupNextButton() {
        binding.nextButton.setOnClickListener {
            if (selectedEndButton == binding.directSetButton &&
                (selectedYear == null || selectedMonth == null || selectedDay == null)
            ) {
                showEndDateBottomSheet()
            } else {
                goToParticipantAlways()
            }
        }
    }

    private fun showEndDateBottomSheet() {
        val bottomSheet = EndDateBottomSheet()

        // '아니오' 버튼 클릭
        bottomSheet.onNoClicked = {
            goToParticipantAlways()
        }

        bottomSheet.show(parentFragmentManager, "EndDateBottomSheet")
    }

    private fun goToParticipantAlways() {
        val period = selectedPeriodButton?.text?.toString().orEmpty()
        val frequency = binding.frequencyInputEditText.text.toString().toIntOrNull() ?: 0

        val endDateString: String? = when (selectedEndButton) {
            binding.endOption1WeekButton -> endDateFromToday(7)
            binding.endOption1MonthButton -> endDateFromToday(30)
            binding.endOption3MonthButton -> endDateFromToday(90)
            binding.endOption6MonthButton -> endDateFromToday(180)
            binding.endOption1YearButton -> endDateFromToday(365)
            binding.directSetButton -> {
                if (selectedYear != null && selectedMonth != null && selectedDay != null) {
                    "$selectedYear-$selectedMonth-$selectedDay"
                } else {
                    null
                }
            }
            else -> null
        }

        val goalActivity = activity as? GoalActivity
        if (goalActivity != null) {
            goalActivity.period = period
            goalActivity.frequency = frequency
            goalActivity.endDate = endDateString

            val nextFragment = if (goalActivity.isFriendTab) {
                // 다음 프래그먼트로 이동
                ParticipantLimitFragment().apply {
                    arguments = Bundle().apply {
                        putString("goalOwnerName", goalActivity.goalOwnerName)
                        putString("goalType", goalActivity.goalType)
                        putString("goalCategory", goalActivity.goalCategory)
                        putString("goalName", goalActivity.goalName)
                        putString("goalAmount", goalActivity.goalAmount)
                        putString("verificationType", goalActivity.verificationType)
                        putString("period", goalActivity.period)
                        putInt("frequency", goalActivity.frequency)
                        putString("endDate", goalActivity.endDate)
                    }

                }
            }else {
                goalActivity.limitFriendCount = Int.MAX_VALUE
                PushAlertCommunityFragment().apply {
                    arguments = Bundle().apply {
                        putString("goalOwnerName", goalActivity.goalOwnerName)
                        putString("goalType", goalActivity.goalType)
                        putString("goalCategory", goalActivity.goalCategory)
                        putString("goalName", goalActivity.goalName)
                        putString("goalAmount", goalActivity.goalAmount)
                        putString("verificationType", goalActivity.verificationType)
                        putString("period", goalActivity.period)
                        putInt("frequency", goalActivity.frequency)
                        putInt("limitFriendCount", Int.MAX_VALUE)
                        putString("endDate", goalActivity.endDate)
                    }
                }
            }
            backStackTrueGoalNav(nextFragment,"GoalDetailFragment")
            return
        }

        val participantFragment = ParticipantLimitFragment().apply {
            arguments = Bundle().apply {
                putString("goalOwnerName", goalOwnerName)
                putString("verificationType", selectedMethod.orEmpty())
                putString("period", period)
                putInt("frequency", frequency)
                putString("endDate", endDateString)
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

    private fun setPeriodListener(button: AppCompatButton){
        selectedPeriodButton?.let { resetButtonStyle(it) }
        selectButtonStyle(button)
        selectedPeriodButton = button
        binding.frequencyInputState.text = button.text
        updateNextButtonState()
    }

    private fun setErrorText(frequency: Int) {
        if (frequency < 1) {
            binding.frequencyErrorText.visibility = View.VISIBLE
            isFrequencyValid = false
        } else {
            binding.frequencyErrorText.visibility = View.GONE
            isFrequencyValid = true
        }
    }

    private fun setEndListener(button: AppCompatButton){
        if (selectedEndButton == button) {
            Log.d("GoalDetailFragment", "selectedEndButton == button")
            Log.d("GoalDetailFragment", "selectedEndButton: $selectedEndButton")
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
    }

    override fun onResume() {
        super.onResume()
        setEdit()
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

    fun setEdit(){
        val activity = (requireActivity() as GoalActivity)
        selectedMethod = arguments?.getString("SELECTED_METHOD")
        goalOwnerName = activity.goalOwnerName
        val goalDataE = if (viewModel.editGoalData != null){
            viewModel.editGoalData!!.run {
                val data = copy(goalName = activity.goalName, goalAmount = activity.goalAmount,
                    verificationType = activity.verificationType,)
                equil(
                    when(data.verificationType){
                        "PICTURE" -> data.copy(oneDose = activity.oneDose.toIntOrNull() ?: 0)
                        else -> data.copy(goalTime = activity.goalTime)
                    })
            }
        }else false
        titleFormat(activity.isFriendTab,goalDataE, binding.friendGoalTitle,
            if (viewModel.friendNickname != "사용자")viewModel.friendNickname
            else activity.goalOwnerName){
            binding.nextButton.isEnabled = false
        }
        logGoalActivityData()
        Log.d("GoalDetailFragment", "goal : ${viewModel.editGoalData}")
        // 주기 설정
        val period = when(activity.period){
            "DAY"-> "매일"
            "WEEK"-> "매주"
            "MONTH"-> "매달"
            else -> activity.period
        }
        endOptionButtons = monthEndUi(period)
        periodButtons.forEach {
            if (it.text.toString() == period) {
                selectedPeriodButton?.let { resetButtonStyle(it) }
                selectButtonStyle(it)
                selectedPeriodButton = it
                binding.frequencyInputState.text = it.text
            }
        }


        Log.d("GoalDetailFragment", viewModel.editGoalData.toString())
        // 빈도 설정
        if (activity.frequency != 0) {
            binding.frequencyInputEditText.setText("${activity.frequency}")
            setErrorText(activity.frequency)
        }

        // 종료일 설정
        selectedEndButton = null
        Log.d("GoalDetailFragment", "endDate: ${activity.endDate}")
        if (!activity.endDate.isNullOrBlank()) {
            val endDate = daysFromToday(activity.endDate!!)
            if (endDate == -1) return
            when (endDate) {
                7 -> setEndListener(endOptionButtons[0])
                30 -> setEndListener(endOptionButtons[if(period == "매달")0 else 1])
                90 -> setEndListener(endOptionButtons[if(period == "매달")1 else 2])
                180 -> setEndListener(endOptionButtons[if(period == "매달")2 else 3])
                365 -> setEndListener(endOptionButtons[if(period == "매달")3 else 4])
                else -> {
                    Log.d("GoalDetailFragment", "endDate: ${activity.endDate}")

                    val dayText = activity.endDate?.split("-")
                    if (dayText != null) {
                        if (dayText.size == 3) {
                            binding.challengeYearTv.text = getString(R.string.year_unit, dayText[0])
                            selectedYear = dayText[0]
                            binding.challengeMonthTv.text = getString(R.string.month_unit, dayText[1])
                            selectedMonth = dayText[1]
                            binding.challengeDayTv.text = getString(R.string.day_unit, dayText[2])
                            selectedDay = dayText[2]
                        }
                    }
                    setEndListener(endOptionButtons.last())
                }
            }
        }



        updateNextButtonState()
    }

    private fun monthEndUi(period: String): List<AppCompatButton>
    = if(period == "매달"||period == "MONTH") {
            _endOptionButtons.drop(1)
        }else _endOptionButtons
}
