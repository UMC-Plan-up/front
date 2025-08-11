package com.example.planup.goal.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity

class CertificationMethodFragment : Fragment(R.layout.fragment_certification_method) {

    private lateinit var backIcon: ImageView
    private lateinit var nextButton: AppCompatButton

    private lateinit var timerAuthLayout: LinearLayout
    private lateinit var pictureAuthLayout: LinearLayout

    private var selectedMethod: String? = null // "TIMER" or "PICTURE"

    private lateinit var goalOwnerName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GoalInputFragment에서 전달한 닉네임 받기
        goalOwnerName = arguments?.getString("goalOwnerName")
            ?: (activity as? GoalActivity)?.goalOwnerName
                    ?: "사용자"

        // 초기화
        backIcon = view.findViewById(R.id.backIcon)
        nextButton = view.findViewById(R.id.nextButton)
        timerAuthLayout = view.findViewById(R.id.timerAuthLayout)
        pictureAuthLayout = view.findViewById(R.id.pictureAuthLayout)

        val goalDetailTitle: TextView = view.findViewById(R.id.goalDetailTitle)

        goalDetailTitle.text = getString(R.string.goal_friend_detail, goalOwnerName)

        // 다음 버튼 비활성화
        setNextButtonEnabled(false)

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /* 타이머 인증 선택 */
        timerAuthLayout.setOnClickListener {
            selectAuthMethod("TIMER")
        }

        /* 사진 인증 선택 */
        pictureAuthLayout.setOnClickListener {
            selectAuthMethod("PICTURE")
        }

        /* 다음 버튼 클릭 시 */
        nextButton.setOnClickListener {
            val activity = requireActivity() as GoalActivity

            selectedMethod?.let { method ->
                activity.verificationType = method

                when (method) {
                    "TIMER" -> {
                        // 타이머 인증 → TimerSettingFragment로 이동
                        val timerFragment = TimerSettingFragment().apply {
                            arguments = Bundle().apply {
                                putString("goalOwnerName", goalOwnerName) // 닉네임 다시 넘기기
                            }
                        }
                        activity.navigateToFragment(timerFragment)
                    }
                    "PICTURE" -> {
                        // 사진 인증 → GoalDetailFragment로 이동
                        val detailFragment = GoalDetailFragment().apply {
                            arguments = Bundle().apply {
                                putString("goalOwnerName", goalOwnerName) // 닉네임 다시 넘기기
                            }
                        }
                        activity.navigateToFragment(detailFragment)
                    }
                }
            }
        }
    }

    /* 인증 방식 선택 처리 */
    private fun selectAuthMethod(method: String) {
        selectedMethod = method

        resetAuthLayoutStyles()

        // 선택된 항목
        if (method == "TIMER") {
            timerAuthLayout.setBackgroundResource(R.drawable.bg_picture_selected_blue_stroke)
        } else if (method == "PICTURE") {
            pictureAuthLayout.setBackgroundResource(R.drawable.bg_picture_selected_blue_stroke)
        }

        // 인증 방식 선택 후 → 다음 버튼 활성화
        setNextButtonEnabled(true)
    }

    /* 인증 레이아웃 스타일 기본값으로 초기화 */
    private fun resetAuthLayoutStyles() {
        val defaultDrawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.bg_picture_selected
        )

        // 기본 배경/테두리로 복원
        timerAuthLayout.background = defaultDrawable
        pictureAuthLayout.background = defaultDrawable

        timerAuthLayout.backgroundTintList = null
        pictureAuthLayout.backgroundTintList = null
    }

    /* 다음 버튼 활성/비활성 처리 */
    private fun setNextButtonEnabled(enabled: Boolean) {
        nextButton.isEnabled = enabled
        if (enabled) {
            nextButton.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.btn_next_background
            )
        } else {
            nextButton.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.btn_next_background_gray
            )
        }
    }
}
