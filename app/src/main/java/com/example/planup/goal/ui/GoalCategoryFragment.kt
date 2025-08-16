package com.example.planup.goal.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.databinding.FragmentGoalCategoryBinding

class GoalCategoryFragment : Fragment() {

    private var _binding: FragmentGoalCategoryBinding? = null
    private val binding get() = _binding!!

    private var selectedGoalLayout: LinearLayout? = null
    private lateinit var allCategoryLayouts: List<LinearLayout>

    private var selectedCategory: LinearLayout? = null
    private var isCustomMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            requireActivity().finish()
        }

        allCategoryLayouts = listOf(
            binding.categoryStudyLayout,
            binding.categoryReadingLayout,
            binding.categoryDigitalDetoxLayout,
            binding.categoryMeditationLayout,
            binding.categorySleepLayout,
            binding.categoryInstrumentLayout,
            binding.categoryExerciseLayout,
            binding.categoryDiaryLayout,
            binding.categoryCustomLayout
        )

        binding.categorySettingTitle.visibility = View.GONE
        allCategoryLayouts.forEach { it.visibility = View.GONE }
        binding.customCategoryEditText.visibility = View.GONE

        binding.communityGoalLayout.setOnClickListener { handleGoalSelection(binding.communityGoalLayout) }
        binding.challengeGoalLayout.setOnClickListener { handleGoalSelection(binding.challengeGoalLayout) }

        allCategoryLayouts.forEach { layout ->
            layout.setOnClickListener { handleCategorySelection(layout) }
        }

        binding.customCategoryEditText.addTextChangedListener { checkNextButtonEnabled() }

        binding.nextButton.isEnabled = false

        /* 다음 버튼 클릭 시 */
        binding.nextButton.setOnClickListener {
            if (isCustomMode && binding.customCategoryEditText.text.isNullOrBlank()) {
                binding.categoryGuideText2.visibility = View.VISIBLE
                return@setOnClickListener
            } else if (!isCustomMode && selectedCategory == null) {
                binding.categoryGuideText1.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val goalOwnerName = arguments?.getString("goalOwnerName") ?: "사용자"

            val selectedCategoryText = if (isCustomMode) {
                binding.customCategoryEditText.text.toString()
            } else {
                selectedCategory?.findViewById<TextView>(
                    getCategoryTextId(selectedCategory!!.id)
                )?.text.toString()
            }


            val activity = requireActivity() as GoalActivity
            activity.goalType = if (selectedGoalLayout?.id == R.id.challengeGoalLayout) "challenge" else "community"
            activity.goalCategory = selectedCategoryText


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
                binding.customCategoryEditText.isFocused
            ) {
                binding.customCategoryEditText.clearFocus()
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

        binding.categorySettingTitle.visibility = View.VISIBLE
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
        binding.customCategoryEditText.visibility = if (isCustomMode) View.VISIBLE else View.GONE

        binding.categoryGuideText1.visibility = View.GONE
        binding.categoryGuideText2.visibility = View.GONE

        checkNextButtonEnabled()
    }

    // 카테고리 선택 해제
    private fun resetCategoryStyle(layout: LinearLayout) {
        val textView = layout.findViewById<TextView>(getCategoryTextId(layout.id))
        layout.backgroundTintList = null
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent6))
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
        binding.nextButton.isEnabled =
            (selectedCategory != null && !isCustomMode) ||
                    (isCustomMode && !binding.customCategoryEditText.text.isNullOrBlank())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
