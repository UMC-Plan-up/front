package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.main.home.ui.HomeFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.planup.databinding.FragmentGoalCompleteBinding

class GoalCompleteFragment : Fragment() {

    private var _binding: FragmentGoalCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        applySpannableText()
    }

    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
    private fun setupClickListeners() {
        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /* Plan-Up 사용 시작하기 버튼 */
        binding.startPlanUpButton.setOnClickListener {
            // 2초 후 HomeFragment로 이동
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000)
                (requireActivity() as GoalActivity)
                    .navigateToFragment(HomeFragment())
            }
        }
    }

    /* Push 허용 여부 저장 */
    private fun savePushAlertEnabled(enabled: Boolean) {
        // TODO: Push 허용 여부 저장
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

            binding.goalInfoText.text = spannable
        } else {
            binding.goalInfoText.text = fullText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}