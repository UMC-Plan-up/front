package com.example.planup.password.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.password.ResetPasswordActivity

class FindPasswordEmailSentFragment : Fragment(R.layout.fragment_find_password_email_sent) {

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* FindPasswordFragment에서 넘어올 때 이메일 전달받기 */
        userEmail = arguments?.getString("email")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backIcon = view.findViewById<ImageView>(R.id.backIcon)
        val resendInfoText = view.findViewById<TextView>(R.id.resendInfoText)
        val emailSentDescription = view.findViewById<TextView>(R.id.emailSentDescription)
        val mockVerifyText = view.findViewById<TextView>(R.id.mockVerifyText) // [테스트용] 인증 성공 텍스트

        /* 전달받은 이메일 적용 */
        userEmail?.let { email ->
            emailSentDescription.text = "($email) 인증을 위한 \n링크를 보내드렸어요. \n메일함에서 링크를 확인해 주세요."
        }


        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.resetPasswordContainer, FindPasswordFragment()) // 이전 화면
                .addToBackStack(null)
                .commit()
        }

        /* "이메일을 받지 못하셨나요?" 텍스트 클릭 → BottomSheet 표시 */
        resendInfoText.setOnClickListener {
            val bottomSheet = ResendEmailBottomSheet()
            bottomSheet.show(parentFragmentManager, "ResendEmailBottomSheet")
        }


        /* [테스트용] 클릭 시 (인증 성공 가정) → ResetPasswordFragment로 이동 */
        mockVerifyText.setOnClickListener {
            openResetPasswordStep()
        }
    }

    /* ResetPasswordFragment로 이동 */
    private fun openResetPasswordStep() {
        // ResetPasswordFragment로 이동할 때 이메일 같이 넘기기
        val fragment = ResetPasswordFragment().apply {
            arguments = Bundle().apply {
                putString("email", userEmail) // 이메일 전달
            }
        }

        (requireActivity() as ResetPasswordActivity)
            .navigateToFragment(fragment)
    }

    companion object {
        fun newInstance(email: String): FindPasswordEmailSentFragment {
            val fragment = FindPasswordEmailSentFragment()
            val args = Bundle()
            args.putString("email", email)
            fragment.arguments = args
            return fragment
        }
    }
}
