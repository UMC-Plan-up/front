package com.planup.planup.goal.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.planup.planup.R
import com.planup.planup.databinding.FragmentGoalInputBinding
import com.planup.planup.goal.GoalActivity

class GoalInputFragment : Fragment() {

    private var _binding: FragmentGoalInputBinding? = null
    private val binding get() = _binding!!

    private var goalOwnerName: String = "사용자"
    private var goalType: String? = null
    private var goalCategory: String? = null

    private val prefs by lazy {
        requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goalOwnerName = arguments?.getString("goalOwnerName") ?: "사용자"
        goalType = arguments?.getString("goalType")
        goalCategory = arguments?.getString("selectedCategory")

        goalType?.let { prefs.edit().putString(KEY_GOAL_TYPE, it).apply() }
        goalCategory?.let { prefs.edit().putString(KEY_GOAL_CATEGORY, it).apply() }
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

        // 타이틀에 닉네임 반영

        if ((requireActivity() as GoalActivity).isFriendTab) {
            binding.friendGoalTitleText.text = getString(R.string.goal_friend_detail, goalOwnerName)
            binding.friendGoalDescriptionText.visibility = View.VISIBLE
        }else {
            binding.friendGoalTitleText.text = getString(R.string.goal_community_detail)
            binding.friendGoalDescriptionText.visibility = View.GONE
        }

        binding.goalNameMinLengthHint.visibility = View.GONE
        binding.goalNameMaxLengthHint.visibility = View.GONE
        binding.goalVolumeMinLengthHint.visibility = View.GONE
        binding.goalVolumeMaxLengthHint.visibility = View.GONE

        // 처음엔 다음 버튼 비활성화
        setNextButtonEnabled(isGoalNameValid() && isGoalVolumeValid())

        // 뒤로가기
        binding.backIcon.setOnClickListener { parentFragmentManager.popBackStack() }

        // 포커스 시 최소 입력 힌트 노출
        binding.nicknameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.nicknameEditText.text.isNullOrEmpty()) {
                binding.goalNameMinLengthHint.visibility = View.VISIBLE
            }
        }
        binding.goalVolumeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.goalVolumeEditText.text.isNullOrEmpty()) {
                binding.goalVolumeMinLengthHint.visibility = View.VISIBLE
            }
        }

        // 입력 감지 → 유효성 & 즉시 저장
        binding.nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                prefs.edit().putString(KEY_GOAL_NAME, s?.toString().orEmpty()).apply()
                validateInputs()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.goalVolumeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                prefs.edit().putString(KEY_GOAL_AMOUNT, s?.toString().orEmpty()).apply()
                validateInputs()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 다음 버튼
        binding.nextButton.setOnClickListener {
            if (!isGoalNameValid() || !isGoalVolumeValid()) return@setOnClickListener

            val activity = requireActivity() as GoalActivity

            // 액티비티에도 넣기
            activity.goalName = binding.nicknameEditText.text.toString()
            activity.goalAmount = binding.goalVolumeEditText.text.toString()
            goalType?.let { activity.goalType = it }
            goalCategory?.let { activity.goalCategory = it }

            prefs.edit()
                .putString(KEY_GOAL_NAME, activity.goalName)
                .putString(KEY_GOAL_AMOUNT, activity.goalAmount)
                .putString(KEY_GOAL_TYPE, activity.goalType)
                .putString(KEY_GOAL_CATEGORY, activity.goalCategory)
                .apply()

            // 다음 화면으로
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

    // 전체 입력 검증 → 버튼 활성화
    private fun validateInputs() {
        val goalNameValid = isGoalNameValid()
        val goalVolumeValid = isGoalVolumeValid()
        setNextButtonEnabled(goalNameValid && goalVolumeValid)
    }

    // 목표명 검증
    private fun isGoalNameValid(): Boolean {
        val text = binding.nicknameEditText.text?.toString().orEmpty()
        val length = text.length
        var valid = true

        if (length < 1) {
            if (binding.nicknameEditText.hasFocus()) {
                binding.goalNameMinLengthHint.visibility = View.VISIBLE
            }
            valid = false
        } else {
            binding.goalNameMinLengthHint.visibility = View.GONE
        }

        if (length > 20) {
            binding.goalNameMaxLengthHint.visibility = View.VISIBLE
            valid = false
        } else {
            binding.goalNameMaxLengthHint.visibility = View.GONE
        }

        return valid
    }

    // 1회 분량 검증
    private fun isGoalVolumeValid(): Boolean {
        val text = binding.goalVolumeEditText.text?.toString().orEmpty()
        val length = text.length
        var valid = true

        if (length < 1) {
            if (binding.goalVolumeEditText.hasFocus()) {
                binding.goalVolumeMinLengthHint.visibility = View.VISIBLE
            }
            valid = false
        } else {
            binding.goalVolumeMinLengthHint.visibility = View.GONE
        }

        if (length > 30) {
            binding.goalVolumeMaxLengthHint.visibility = View.VISIBLE
            valid = false
        } else {
            binding.goalVolumeMaxLengthHint.visibility = View.GONE
        }

        return valid
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun setNextButtonEnabled(enabled: Boolean) {
        binding.nextButton.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PREFS_NAME = "user_data"
        private const val KEY_GOAL_NAME = "last_goal_name"
        private const val KEY_GOAL_AMOUNT = "last_goal_amount"
        private const val KEY_GOAL_TYPE = "last_goal_type"
        private const val KEY_GOAL_CATEGORY = "last_goal_category"
    }
}
