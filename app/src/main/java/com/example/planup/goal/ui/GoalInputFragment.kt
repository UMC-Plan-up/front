package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.databinding.FragmentGoalInputBinding

class GoalInputFragment : Fragment() {

    private var _binding: FragmentGoalInputBinding? = null
    private val binding get() = _binding!!

    private var goalOwnerName: String = "사용자"

    private var goalType: String? = null
    private var goalCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goalOwnerName = arguments?.getString("goalOwnerName") ?: "사용자"
        goalType = arguments?.getString("goalType")
        goalCategory = arguments?.getString("selectedCategory")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 닉네임 반영
        binding.friendGoalTitleText.text = getString(R.string.goal_friend_detail, goalOwnerName)

        binding.goalNameMinLengthHint.visibility = View.GONE
        binding.goalNameMaxLengthHint.visibility = View.GONE
        binding.goalVolumeMinLengthHint.visibility = View.GONE
        binding.goalVolumeMaxLengthHint.visibility = View.GONE

        /* 처음엔 다음 버튼 비활성화 */
        setNextButtonEnabled(false)

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /* 목표명 EditText 클릭 시 */
        binding.nicknameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.nicknameEditText.text.isNullOrEmpty()) {
                binding.goalNameMinLengthHint.visibility = View.VISIBLE
            }
        }

        /* 1회 분량 EditText 클릭 시 */
        binding.goalVolumeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.goalVolumeEditText.text.isNullOrEmpty()) {
                binding.goalVolumeMinLengthHint.visibility = View.VISIBLE
            }
        }

        /* 입력 감지 → 유효성 검증 */
        binding.nicknameEditText.addTextChangedListener(inputWatcher)
        binding.goalVolumeEditText.addTextChangedListener(inputWatcher)

        /* 다음 버튼 클릭 */
        binding.nextButton.setOnClickListener {
            if (!isGoalNameValid() || !isGoalVolumeValid()) return@setOnClickListener

            val activity = requireActivity() as GoalActivity

            activity.goalName = binding.nicknameEditText.text.toString()
            activity.goalAmount = binding.goalVolumeEditText.text.toString()


            goalType?.let { activity.goalType = it }
            goalCategory?.let { activity.goalCategory = it }

            // 다음 프래그먼트로 이동
            val certificationFragment = CertificationMethodFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                    putString("goalName", activity.goalName)
                    putString("goalAmount", activity.goalAmount)
                    putString("goalType", activity.goalType)
                    putString("goalCategory", activity.goalCategory)
                }
            }
            activity.navigateToFragment(certificationFragment)
        }

        // 바깥 터치 시 키보드 숨김
        view.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN &&
                (binding.nicknameEditText.isFocused || binding.goalVolumeEditText.isFocused)
            ) {
                binding.nicknameEditText.clearFocus()
                binding.goalVolumeEditText.clearFocus()
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
        setNextButtonEnabled(goalNameValid && goalVolumeValid)
    }

    /* 목표명 조건 검증 */
    private fun isGoalNameValid(): Boolean {
        val text = binding.nicknameEditText.text?.toString() ?: ""
        val length = text.length

        var valid = true

        // (1) 1글자 미만 → 클릭 후에만 에러 표시
        if (length < 1) {
            if (binding.nicknameEditText.hasFocus()) { // 포커스 있을 때만 표시
                binding.goalNameMinLengthHint.visibility = View.VISIBLE
            }
            valid = false
        } else {
            binding.goalNameMinLengthHint.visibility = View.GONE
        }

        // (2) 20자 초과 → 에러 표시
        if (length > 20) {
            binding.goalNameMaxLengthHint.visibility = View.VISIBLE
            valid = false
        } else {
            binding.goalNameMaxLengthHint.visibility = View.GONE
        }

        return valid
    }

    /* 1회 분량 조건 검증 */
    private fun isGoalVolumeValid(): Boolean {
        val text = binding.goalVolumeEditText.text?.toString() ?: ""
        val length = text.length

        var valid = true

        // (1) 1글자 미만 → 클릭 후에만 에러 표시
        if (length < 1) {
            if (binding.goalVolumeEditText.hasFocus()) { // 포커스 있을 때만 표시
                binding.goalVolumeMinLengthHint.visibility = View.VISIBLE
            }
            valid = false
        } else {
            binding.goalVolumeMinLengthHint.visibility = View.GONE
        }

        // (2) 30자 초과 → 에러 표시
        if (length > 30) {
            binding.goalVolumeMaxLengthHint.visibility = View.VISIBLE
            valid = false
        } else {
            binding.goalVolumeMaxLengthHint.visibility = View.GONE
        }

        return valid
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /* 다음 버튼 활성 ↔ 비활성 */
    private fun setNextButtonEnabled(enabled: Boolean) {
        binding.nextButton.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}