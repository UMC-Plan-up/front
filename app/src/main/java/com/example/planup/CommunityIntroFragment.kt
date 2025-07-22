package com.example.planup

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment

class CommunityIntroFragment : Fragment(R.layout.fragment_community_intro) {

    private lateinit var backIcon: ImageView
    private lateinit var titleText: TextView
    private lateinit var startGoalSettingButton: AppCompatButton

    // 닉네임 키 상수
    companion object {
        private const val ARG_NICKNAME = "nickname"

        fun newInstance(nickname: String): CommunityIntroFragment {
            val fragment = CommunityIntroFragment()
            val args = Bundle()
            args.putString(ARG_NICKNAME, nickname)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backIcon = view.findViewById(R.id.backIcon)
        titleText = view.findViewById(R.id.titleText)
        startGoalSettingButton = view.findViewById(R.id.startGoalSettingButton)

        // 전달받은 닉네임 적용
        val nickname = arguments?.getString(ARG_NICKNAME) ?: "사용자"
        titleText.text = "다양한 커뮤니티들이\n${nickname}님을 기다리고 있어요!"


        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeInputFragment())
        }

        /* 목표 설정 시작하기 버튼 → GoalCategoryFragment로 이동 */
        startGoalSettingButton.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(GoalCategoryFragment())
        }
    }
}
