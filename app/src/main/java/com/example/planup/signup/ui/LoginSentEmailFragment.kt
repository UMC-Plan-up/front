package com.example.planup.signup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.password.ui.ResendEmailBottomSheet
import com.example.planup.signup.SignupActivity

class LoginSentEmailFragment : Fragment(R.layout.fragment_login_sent_email) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        val backIcon = view.findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val resendText = view.findViewById<TextView>(R.id.resendText)
        resendText.setOnClickListener {
            // BottomSheet 띄우기
            ResendEmailBottomSheet().show(parentFragmentManager, "ResendEmailBottomSheet")
        }

        /* [테스트용] 클릭 시 (인증 성공 가정) → 프로필 설정 화면으로 이동 */
        // TODO: 이메일 인증 API 호출 → 성공 응답 시 ProfileSetupFragment로 이동하도록 변경
        val mockVerifyText = view.findViewById<TextView>(R.id.mockVerifyText)
        mockVerifyText.setOnClickListener {
//            val intent = Intent(context as SignupActivity,GoalActivity::class.java)
//            startActivity(intent)
            openProfileSetup()
        }
    }

    /* ProfileSetupFragment로 이동하는 메서드 */
    private fun openProfileSetup() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.signup_container,
                ProfileSetupFragment()
            )
            .addToBackStack(null)
            .commit()
    }
}

