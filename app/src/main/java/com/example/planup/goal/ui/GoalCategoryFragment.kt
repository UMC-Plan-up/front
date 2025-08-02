package com.example.planup.goal.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity




class GoalCategoryFragment : Fragment(R.layout.fragment_goal_category) {

    private lateinit var backIcon: ImageView
    private lateinit var nextButton: AppCompatButton

    private lateinit var communityGoalLayout: LinearLayout
    private lateinit var challengeGoalLayout: LinearLayout
    private var selectedGoalLayout: LinearLayout? = null

    private lateinit var categorySettingTitle: TextView
    private lateinit var allCategoryLayouts: List<LinearLayout>

    private lateinit var categoryErrorText: TextView
    private lateinit var customCategoryErrorText: TextView
    private lateinit var customCategoryEditText: EditText

    private var selectedCategory: LinearLayout? = null
    private var isCustomMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기화
        backIcon = view.findViewById(R.id.backIcon)
        nextButton = view.findViewById(R.id.nextButton)

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            requireActivity().finish()
        }

        communityGoalLayout = view.findViewById(R.id.communityGoalLayout)
        challengeGoalLayout = view.findViewById(R.id.challengeGoalLayout)
        categorySettingTitle = view.findViewById(R.id.categorySettingTitle)

        val categoryStudy = view.findViewById<LinearLayout>(R.id.categoryStudyLayout)
        val categoryReading = view.findViewById<LinearLayout>(R.id.categoryReadingLayout)
        val categoryDigitalDetox = view.findViewById<LinearLayout>(R.id.categoryDigitalDetoxLayout)
        val categoryMeditation = view.findViewById<LinearLayout>(R.id.categoryMeditationLayout)
        val categorySleep = view.findViewById<LinearLayout>(R.id.categorySleepLayout)
        val categoryInstrument = view.findViewById<LinearLayout>(R.id.categoryInstrumentLayout)
        val categoryExercise = view.findViewById<LinearLayout>(R.id.categoryExerciseLayout)
        val categoryDiary = view.findViewById<LinearLayout>(R.id.categoryDiaryLayout)
        val categoryCustom = view.findViewById<LinearLayout>(R.id.categoryCustomLayout)

        allCategoryLayouts = listOf(
            categoryStudy, categoryReading, categoryDigitalDetox, categoryMeditation,
            categorySleep, categoryInstrument, categoryExercise, categoryDiary, categoryCustom
        )

        categorySettingTitle.visibility = View.GONE
        allCategoryLayouts.forEach { it.visibility = View.GONE }
        customCategoryEditText = view.findViewById(R.id.customCategoryEditText)
        customCategoryEditText.visibility = View.GONE

        categoryErrorText = view.findViewById(R.id.categoryGuideText1)
        customCategoryErrorText = view.findViewById(R.id.categoryGuideText2)

        communityGoalLayout.setOnClickListener { handleGoalSelection(communityGoalLayout) }
        challengeGoalLayout.setOnClickListener { handleGoalSelection(challengeGoalLayout) }

        allCategoryLayouts.forEach { layout ->
            layout.setOnClickListener { handleCategorySelection(layout) }
        }

        customCategoryEditText.addTextChangedListener { checkNextButtonEnabled() }

        nextButton.isEnabled = false

        /* 다음 버튼 클릭 시 */
        nextButton.setOnClickListener {
            if (isCustomMode && customCategoryEditText.text.isNullOrBlank()) {
                customCategoryErrorText.visibility = View.VISIBLE
                return@setOnClickListener
            } else if (!isCustomMode && selectedCategory == null) {
                categoryErrorText.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val goalOwnerName = arguments?.getString("goalOwnerName") ?: "사용자"

            val selectedCategoryText = if (isCustomMode) {
                customCategoryEditText.text.toString()
            } else {
                selectedCategory?.findViewById<TextView>(
                    getCategoryTextId(selectedCategory!!.id)
                )?.text.toString()
            }

            val nextFragment = if (selectedGoalLayout?.id == R.id.challengeGoalLayout) {
                GoalInputFragment()
            } else {
                CommonGoalFragment()
            }

            nextFragment.arguments = Bundle().apply {
                putString("goalOwnerName", goalOwnerName)
                putString("selectedCategory", selectedCategoryText)
            }

            (requireActivity() as GoalActivity).navigateToFragment(nextFragment)
        }

        view.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN &&
                customCategoryEditText.isFocused
            ) {
                customCategoryEditText.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }
    }


    // 목표 선택
    private fun handleGoalSelection(layout: LinearLayout) {
        selectedGoalLayout?.let { prev ->
            prev.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_goal_black)
            val prevTitle = prev.findViewById<TextView>(getGoalTextId(prev.id))
            val prevSubTitle = prev.findViewById<TextView?>(R.id.challengeGoalSubtitle)
            prevTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_400))
            prevSubTitle?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_300))
        }

        layout.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_goal_selected_stroke)
        val titleView = layout.findViewById<TextView>(getGoalTextId(layout.id))
        val subTitleView = layout.findViewById<TextView?>(R.id.challengeGoalSubtitle)
        titleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_200))
        subTitleView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_200))

        selectedGoalLayout = layout

        categorySettingTitle.visibility = View.VISIBLE
        allCategoryLayouts.forEach { it.visibility = View.VISIBLE }
    }

    private fun getGoalTextId(layoutId: Int): Int {
        return when (layoutId) {
            R.id.communityGoalLayout -> R.id.communityGoalText
            R.id.challengeGoalLayout -> R.id.challengeGoalTitle
            else -> R.id.communityGoalText
        }
    }

    // 카테고리 선택 처리
    private fun handleCategorySelection(layout: LinearLayout) {
        selectedCategory?.let { resetCategoryStyle(it) }

        val textView = layout.findViewById<TextView>(getCategoryTextId(layout.id))
        layout.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_200))
        textView.setTextColor(Color.WHITE)

        selectedCategory = layout
        isCustomMode = (layout.id == R.id.categoryCustomLayout)
        customCategoryEditText.visibility = if (isCustomMode) View.VISIBLE else View.GONE

        categoryErrorText.visibility = View.GONE
        customCategoryErrorText.visibility = View.GONE

        checkNextButtonEnabled()
    }

    // 카테고리 선택 해제
    private fun resetCategoryStyle(layout: LinearLayout) {
        val textView = layout.findViewById<TextView>(getCategoryTextId(layout.id))
        layout.backgroundTintList = null
        textView.setTextColor(Color.parseColor("#448AF7"))
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun getCategoryTextId(layoutId: Int): Int {
        return when (layoutId) {
            R.id.categoryStudyLayout -> R.id.categoryStudyText
            R.id.categoryReadingLayout -> R.id.categoryReadingText
            R.id.categoryDigitalDetoxLayout -> R.id.categoryDigitalDetoxText
            R.id.categoryMeditationLayout -> R.id.categoryMeditationText
            R.id.categorySleepLayout -> R.id.category_sleep_tv
            R.id.categoryInstrumentLayout -> R.id.categoryInstrumentText
            R.id.categoryExerciseLayout -> R.id.categoryExerciseText
            R.id.categoryDiaryLayout -> R.id.categoryDiaryText
            R.id.categoryCustomLayout -> R.id.category_custom_tv
            else -> R.id.categoryStudyText
        }
    }

    // 다음 버튼 활성화 조건 검사
    private fun checkNextButtonEnabled() {
        nextButton.isEnabled =
            (selectedCategory != null && !isCustomMode) ||
                    (isCustomMode && !customCategoryEditText.text.isNullOrBlank())
    }
}
