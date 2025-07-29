package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalSettingActivity

class GoalCompleteFragment : Fragment(R.layout.fragment_goal_complete) {

    private lateinit var backIcon: ImageView
    private lateinit var startPlanUpButton: AppCompatButton
    private lateinit var goalInfoText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backIcon = view.findViewById(R.id.backIcon)
        startPlanUpButton = view.findViewById(R.id.startPlanUpButton)
        goalInfoText = view.findViewById(R.id.goalInfoText)

        setupClickListeners()
        applySpannableText()
    }

    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
    private fun setupClickListeners() {
        backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /* Plan-Up 사용 시작하기 버튼 */
        startPlanUpButton.setOnClickListener {
            // PushAlertFragment로 이동 (화면 먼저 전환)
            (requireActivity() as GoalSettingActivity)
                .navigateToFragment(PushAlertFragment.newInstance(showPopup = true))
        }

    }

    /* Push 알림 설정 팝업 띄우기 */
    private fun showPushAlertPopup() {
        val dialog = PushAlertPopupDialog(requireContext())

        dialog.setOnConfirmClickListener {
            // [네] 클릭 → Push 허용 저장 후 PushAlertFragment 이동
            savePushAlertEnabled(true)
            goToPushAlertFragment()
        }

        dialog.setOnCancelClickListener {
            // [아니오] 클릭 → TODO: 나중에 다른 화면 이동할 예정
            savePushAlertEnabled(false)
        }

        dialog.show()
    }

    /* Push 허용 여부 저장 */
    private fun savePushAlertEnabled(enabled: Boolean) {
        // TODO: Push 허용 여부 저장
    }

    /* PushAlertFragment 이동 */
    private fun goToPushAlertFragment() {
        (requireActivity() as GoalSettingActivity)
            .navigateToFragment(PushAlertFragment())
    }

    /* goalInfoText에서 마지막 ‘목표’ 단어만 파란색 */
    private fun applySpannableText() {
        val fullText =
            "플랜업에서 설정한 목표들은 기본적으로 친구와 공유됩니다. 목표 공유 설정은 추후에도 ‘목표’ 탭에서 변경 가능해요!"
        val targetWordWithQuote = "‘목표’"

        // 마지막 "‘목표’" 등장 위치 찾기
        val startIndex = fullText.lastIndexOf(targetWordWithQuote)

        if (startIndex >= 0) {
            val endIndex = startIndex + targetWordWithQuote.length
            val spannable = SpannableString(fullText)

            val color = ContextCompat.getColor(requireContext(), R.color.blue_200)

            // 마지막 "‘목표’"만 색상 적용
            spannable.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            goalInfoText.text = spannable
        } else {
            goalInfoText.text = fullText
        }
    }
}
