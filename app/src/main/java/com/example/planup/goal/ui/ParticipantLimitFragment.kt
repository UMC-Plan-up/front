package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.databinding.FragmentParticipantLimitBinding

class ParticipantLimitFragment : Fragment() {

    private var _binding: FragmentParticipantLimitBinding? = null
    private val binding get() = _binding!!

    private var isInputValid = false
    private var goalOwnerName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParticipantLimitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GoalDetailFragment에서 닉네임 받기
        goalOwnerName = arguments?.getString("goalOwnerName")
            ?: (activity as? GoalActivity)?.goalOwnerName
                    ?: "사용자"

        // 닉네임 반영
        binding.friendGoalTitleText.text = getString(R.string.goal_friend_detail, goalOwnerName)
        binding.participantLimitErrorText.visibility = View.GONE

        /* 처음엔 다음 버튼 비활성화 */
        disableNextButton()

        setupClickListeners()
        setupInputValidation()

        view.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN &&
                binding.participantLimitEditText.isFocused
            ) {
                binding.participantLimitEditText.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }
    }

    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
    private fun setupClickListeners() {
        binding.backIcon.setOnClickListener {
            val goalDetailFragment = GoalDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }
            val ga = activity as? GoalActivity
            if (ga != null) {
                ga.navigateToFragment(goalDetailFragment)
            } else {
                val containerId = (view?.parent as? ViewGroup)?.id
                if (containerId != null && containerId != View.NO_ID) {
                    parentFragmentManager.beginTransaction()
                        .replace(containerId, goalDetailFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, goalDetailFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                }
            }
        }

        // 다음 버튼 → PushAlertFragment 이동
        binding.nextButton.setOnClickListener {
            if (isInputValid) {
                val limit = binding.participantLimitEditText.text.toString().toIntOrNull() ?: 0
                val pushAlertFragment = PushAlertFragment().apply {
                    arguments = Bundle().apply {
                        putString("goalOwnerName", goalOwnerName)
                        putInt("limitFriendCount", limit)
                    }
                }

                val ga = activity as? GoalActivity
                if (ga != null) {
                    ga.limitFriendCount = limit
                    ga.navigateToFragment(pushAlertFragment)
                } else {
                    val containerId = (view?.parent as? ViewGroup)?.id
                    if (containerId != null && containerId != View.NO_ID) {
                        parentFragmentManager.beginTransaction()
                            .replace(containerId, pushAlertFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(android.R.id.content, pushAlertFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss()
                    }
                }
            }
        }
    }

    /* 참여자 제한 인원 입력 조건 검증 */
    private fun setupInputValidation() {
        // 숫자만 입력 가능
        binding.participantLimitEditText.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]*"))) source else ""
        })

        binding.participantLimitEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull() ?: 0

                if (value < 1) {
                    // 1 미만 → 오류 메시지 표시
                    binding.participantLimitErrorText.visibility = View.VISIBLE
                    disableNextButton()
                    isInputValid = false
                } else {
                    // 1 이상 → 오류 메시지 숨김
                    binding.participantLimitErrorText.visibility = View.GONE
                    enableNextButton()
                    isInputValid = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /* 버튼 비활성화 */
    private fun disableNextButton() {
        binding.nextButton.isEnabled = false
        binding.nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background_gray)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /* 버튼 활성화 */
    private fun enableNextButton() {
        binding.nextButton.isEnabled = true
        binding.nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}