package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity

class ParticipantLimitFragment : Fragment(R.layout.fragment_participant_limit) {

    private lateinit var backIcon: ImageView
    private lateinit var nextButton: AppCompatButton
    private lateinit var participantLimitEditText: EditText
    private lateinit var participantLimitErrorText: TextView
    private lateinit var passwordNotFoundErrorText: TextView
    private lateinit var participantTitleText: TextView
    private lateinit var participantDescriptionText: TextView
    private var isInputValid = false
    private var goalOwnerName: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GoalDetailFragment에서 닉네임 받기
        goalOwnerName = arguments?.getString("goalOwnerName")
            ?: throw IllegalStateException("ParticipantLimitFragment must receive goalOwnerName!")

        // 초기화
        backIcon = view.findViewById(R.id.backIcon)
        nextButton = view.findViewById(R.id.nextButton)
        participantLimitEditText = view.findViewById(R.id.participantLimitEditText)

        // 항상 보이는 설명 텍스트
        participantLimitErrorText = view.findViewById(R.id.participantLimitErrorText)
        // 1 미만일 때만 보이는 오류 텍스트
        passwordNotFoundErrorText = view.findViewById(R.id.passwordNotFoundErrorText)

        participantTitleText = view.findViewById(R.id.friendGoalTitleText)
        participantDescriptionText = view.findViewById(R.id.friendGoalDescriptionText)

        // 닉네임 반영
        participantTitleText.text = getString(R.string.goal_friend_detail, goalOwnerName)
        participantLimitErrorText.visibility = View.GONE

        disableNextButton()

        setupClickListeners()
        setupInputValidation()
    }


    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
    private fun setupClickListeners() {
        backIcon.setOnClickListener {
            val goalDetailFragment = GoalDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }
            (requireActivity() as GoalActivity)
                .navigateToFragment(goalDetailFragment)
        }

        // 다음 버튼 → GoalCompleteFragment 이동
        nextButton.setOnClickListener {
            if (isInputValid) {
                val goalCompleteFragment = GoalCompleteFragment().apply {
                    arguments = Bundle().apply {
                        putString("goalOwnerName", goalOwnerName)
                    }
                }
                (requireActivity() as GoalActivity)
                    .navigateToFragment(goalCompleteFragment)
            }
        }
    }

    /* 참여자 제한 인원 입력 조건 검증 */
    private fun setupInputValidation() {
        // 숫자만 입력 가능
        participantLimitEditText.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]*"))) source else ""
        })

        participantLimitEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull() ?: 0

                if (value < 1) {
                    // 1 미만 → 오류 메시지 표시
                    participantLimitErrorText.visibility = View.VISIBLE
                    disableNextButton()
                    isInputValid = false
                } else {
                    // 1 이상 → 오류 메시지 숨김
                    participantLimitErrorText.visibility = View.GONE
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
        nextButton.isEnabled = false
        nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background_gray)
    }

    /* 버튼 활성화 */
    private fun enableNextButton() {
        nextButton.isEnabled = true
        nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
    }
}
