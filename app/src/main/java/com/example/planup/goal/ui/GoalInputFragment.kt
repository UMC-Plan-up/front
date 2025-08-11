package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity

class GoalInputFragment : Fragment(R.layout.fragment_goal_input) {

    private lateinit var backIcon: ImageView
    private lateinit var nextButton: AppCompatButton

    private lateinit var friendGoalTitleText: TextView
    private lateinit var friendGoalDescriptionText: TextView

    private lateinit var goalNameEditText: EditText
    private lateinit var goalNameMinLengthHint: TextView
    private lateinit var goalNameMaxLengthHint: TextView

    private lateinit var goalVolumeEditText: EditText
    private lateinit var goalVolumeMinLengthHint: TextView
    private lateinit var goalVolumeMaxLengthHint: TextView

    private lateinit var goalOwnerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 닉네임 전달
        goalOwnerName = requireArguments().getString("goalOwnerName")
            ?: throw IllegalStateException("GoalInputFragment must receive goalOwnerName!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기화
        backIcon = view.findViewById(R.id.backIcon)
        nextButton = view.findViewById(R.id.nextButton)

        friendGoalTitleText = view.findViewById(R.id.friendGoalTitleText)
        friendGoalDescriptionText = view.findViewById(R.id.friendGoalDescriptionText)

        // 닉네임 반영
        friendGoalTitleText.text = getString(R.string.goal_friend_detail, goalOwnerName)

        goalNameEditText = view.findViewById(R.id.nicknameEditText)
        goalNameMinLengthHint = view.findViewById(R.id.goalNameMinLengthHint)
        goalNameMaxLengthHint = view.findViewById(R.id.goalNameMaxLengthHint)

        goalVolumeEditText = view.findViewById(R.id.goalVolumeEditText)
        goalVolumeMinLengthHint = view.findViewById(R.id.goalVolumeMinLengthHint)
        goalVolumeMaxLengthHint = view.findViewById(R.id.goalVolumeMaxLengthHint)

        // 처음엔 GONE 상태
        goalNameMinLengthHint.visibility = View.GONE
        goalNameMaxLengthHint.visibility = View.GONE
        goalVolumeMinLengthHint.visibility = View.GONE
        goalVolumeMaxLengthHint.visibility = View.GONE

        /* 처음엔 다음 버튼 비활성화 */
        setNextButtonEnabled(false)

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /* 목표명 EditText 클릭 시 */
        goalNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && goalNameEditText.text.isNullOrEmpty()) {
                goalNameMinLengthHint.visibility = View.VISIBLE
            }
        }

        /* 1회 분량 EditText 클릭 시 */
        goalVolumeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && goalVolumeEditText.text.isNullOrEmpty()) {
                goalVolumeMinLengthHint.visibility = View.VISIBLE
            }
        }

        /* 입력 감지 → 유효성 검증 */
        goalNameEditText.addTextChangedListener(inputWatcher)
        goalVolumeEditText.addTextChangedListener(inputWatcher)

        /* 다음 버튼 클릭 */
        nextButton.setOnClickListener {
            if (!isGoalNameValid() || !isGoalVolumeValid()) return@setOnClickListener

            val activity = requireActivity() as GoalActivity
            activity.goalName = goalNameEditText.text.toString()
            activity.goalAmount = goalVolumeEditText.text.toString()

            val certificationFragment = CertificationMethodFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }
            activity.navigateToFragment(certificationFragment)
        }


        // 바깥 터치 시 키보드 숨김
        view.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN &&
                (goalNameEditText.isFocused || goalVolumeEditText.isFocused)
            ) {
                goalNameEditText.clearFocus()
                goalVolumeEditText.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }
    }

    /* 입력 감지 */
    private val inputWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateInputs()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /* 전체 입력 검증 → 버튼 활성화 여부 */
    private fun validateInputs() {
        val goalNameValid = isGoalNameValid()
        val goalVolumeValid = isGoalVolumeValid()

        // 조건 만족 → 버튼 활성화
        setNextButtonEnabled(goalNameValid && goalVolumeValid)
    }

    /* 목표명 조건 검증 */
    private fun isGoalNameValid(): Boolean {
        val text = goalNameEditText.text?.toString() ?: ""
        val length = text.length

        var valid = true

        // (1) 1글자 미만 → 클릭 후에만 에러 표시
        if (length < 1) {
            if (goalNameEditText.hasFocus()) { // 포커스 있을 때만 표시
                goalNameMinLengthHint.visibility = View.VISIBLE
            }
            valid = false
        } else {
            goalNameMinLengthHint.visibility = View.GONE
        }

        // (2) 20자 초과 → 에러 표시
        if (length > 20) {
            goalNameMaxLengthHint.visibility = View.VISIBLE
            valid = false
        } else {
            goalNameMaxLengthHint.visibility = View.GONE
        }

        return valid
    }

    /* 1회 분량 조건 검증 */
    private fun isGoalVolumeValid(): Boolean {
        val text = goalVolumeEditText.text?.toString() ?: ""
        val length = text.length

        var valid = true

        // (1) 1글자 미만 → 클릭 후에만 에러 표시
        if (length < 1) {
            if (goalVolumeEditText.hasFocus()) { // 포커스 있을 때만 표시
                goalVolumeMinLengthHint.visibility = View.VISIBLE
            }
            valid = false
        } else {
            goalVolumeMinLengthHint.visibility = View.GONE
        }

        // (2) 30자 초과 → 에러 표시
        if (length > 30) {
            goalVolumeMaxLengthHint.visibility = View.VISIBLE
            valid = false
        } else {
            goalVolumeMaxLengthHint.visibility = View.GONE
        }

        return valid
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /* 다음 버튼 활성 ↔ 비활성 */
    private fun setNextButtonEnabled(enabled: Boolean) {
        nextButton.isEnabled = enabled
    }
}
