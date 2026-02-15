package com.planup.planup.main.goal.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.planup.planup.R
import com.planup.planup.databinding.FragmentEditGoalCategoryBinding
import kotlin.collections.forEach

class EditGoalCategoryFragment : Fragment() {
    private var _binding: FragmentEditGoalCategoryBinding? = null
    private val binding get() = _binding!!

    private var selectedGoalLayout: LinearLayout? = null
    private lateinit var allCategoryLayouts: List<LinearLayout>

    private var selectedCategory: LinearLayout? = null
    private var isCustomMode = false
    private lateinit var edittext: EditText
    private lateinit var nextbtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditGoalCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.editCategoryBackIcon.setOnClickListener {
            requireActivity().finish()
        }

        allCategoryLayouts = listOf(
            binding.editCategoryStudyLayout,
            binding.editCategoryReadingLayout,
            binding.editCategoryDigitalDetoxLayout,
            binding.editCategoryMeditationLayout,
            binding.editCategorySleepLayout,
            binding.editCategoryInstrumentLayout,
            binding.editCategoryExerciseLayout,
            binding.editCategoryDiaryLayout,
            binding.editCategoryCustomLayout
        )

        edittext = binding.editCategoryCustomCategoryEditText
        nextbtn = binding.editCategoryNextButton

        //binding.editCategoryCategorySettingTitle.visibility = View.GONE
        //allCategoryLayouts.forEach { it.visibility = View.GONE }
        edittext.visibility = View.GONE


        allCategoryLayouts.forEach { layout ->
            layout.setOnClickListener { handleCategorySelection(layout) }
        }

        edittext.addTextChangedListener { checkNextButtonEnabled() }

        nextbtn.isEnabled = false

        /* 다음 버튼 클릭 시 */
        nextbtn.setOnClickListener {
            // 유효성 검사
            if (isCustomMode && edittext.text.isNullOrBlank()) {
                binding.editCategoryGuideText2.visibility = View.VISIBLE
                return@setOnClickListener
            } else if (!isCustomMode && selectedCategory == null) {
                binding.editCategoryGuideText1.visibility = View.VISIBLE
                return@setOnClickListener
            }


            val selectedCategoryCode = if (isCustomMode) {
                edittext.text.toString()
            } else {
                getCategoryCode(selectedCategory!!.id)
            }

            val titlefragment = EditGoalTitleFragment()
            val goalId = arguments?.getInt("goalId") ?: 0
            Log.d("EditGoalCategoryFragment", "goalId: $goalId")
            titlefragment.arguments = Bundle().apply {
                putString("selectedCategory", selectedCategoryCode)
                putInt("goalId", goalId)
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, titlefragment)
                .addToBackStack(null)
                .commit()
        }

        view.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN &&
                edittext.isFocused
            ) {
                edittext.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }
    }

    // 카테고리 선택 처리
    private fun handleCategorySelection(layout: LinearLayout) {
        selectedCategory?.let { resetCategoryStyle(it) }

        val textView = layout.findViewById<TextView>(getCategoryTextId(layout.id))
        layout.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_200))
        textView.setTextColor(Color.WHITE)

        selectedCategory = layout
        isCustomMode = (layout.id == R.id.edit_category_CustomLayout)
        edittext.visibility = if (isCustomMode) View.VISIBLE else View.GONE

        binding.editCategoryGuideText1.visibility = View.GONE
        binding.editCategoryGuideText2.visibility = View.GONE

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
            R.id.edit_category_StudyLayout -> R.id.edit_category_StudyText
            R.id.edit_category_ReadingLayout -> R.id.edit_category_ReadingText
            R.id.edit_category_DigitalDetoxLayout -> R.id.edit_category_DigitalDetoxText
            R.id.edit_category_MeditationLayout -> R.id.edit_category_MeditationText
            R.id.edit_category_SleepLayout -> R.id.edit_category_sleep_tv
            R.id.edit_category_InstrumentLayout -> R.id.edit_category_InstrumentText
            R.id.edit_category_ExerciseLayout -> R.id.edit_category_ExerciseText
            R.id.edit_category_DiaryLayout -> R.id.edit_category_DiaryText
            R.id.edit_category_CustomLayout -> R.id.edit_category_custom_tv
            else -> R.id.edit_category_StudyText
        }
    }

    private fun getCategoryCode(layoutId: Int): String {
        return when (layoutId) {
            R.id.edit_category_StudyLayout -> "STUDYING"
            R.id.edit_category_ReadingLayout -> "READING"
            R.id.edit_category_DigitalDetoxLayout -> "DIGITAL_DETOX"
            R.id.edit_category_MeditationLayout -> "MEDITATION"
            R.id.edit_category_SleepLayout -> "SLEEP_PATTERN"
            R.id.edit_category_InstrumentLayout -> "MUSIC"
            R.id.edit_category_ExerciseLayout -> "EXERCISE"
            R.id.edit_category_DiaryLayout -> "DIARY"
            R.id.edit_category_CustomLayout -> "SELF"
            else -> "SELF"
        }
    }

    // 다음 버튼 활성화 조건 검사
    private fun checkNextButtonEnabled() {
        nextbtn.isEnabled =
            (selectedCategory != null && !isCustomMode) ||
                    (isCustomMode && !edittext.text.isNullOrBlank())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
