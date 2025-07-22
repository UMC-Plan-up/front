package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.widget.TextView
import com.example.planup.R

class GoalCompleteFragment : Fragment(R.layout.fragment_goal_complete) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.goalInfoText)

        val fullText = "플랜업에서 설정한 목표들은 기본적으로 친구와 공유됩니다. 목표 공유 설정은 추후에도 ‘목표’ 탭에서 변경 가능해요!"
        val targetWordWithQuote = "‘목표’"

        // 마지막으로 등장하는 "‘목표’" 위치 찾기
        val startIndex = fullText.lastIndexOf(targetWordWithQuote)

        if (startIndex >= 0) {
            val endIndex = startIndex + targetWordWithQuote.length
            val spannable = SpannableString(fullText)

            val color = ContextCompat.getColor(requireContext(), R.color.blue_200)

            // 마지막 "‘목표’" 색상 적용
            spannable.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            textView.text = spannable
        } else {
            textView.text = fullText
        }
    }
}
