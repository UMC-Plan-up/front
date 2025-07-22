package com.example.planup.signup.ui

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R

class LoginEmailFragment : Fragment(R.layout.fragment_login_email) {

    private lateinit var emailEditText: EditText
    private lateinit var emailErrorText1: TextView   // 이메일 형식 에러 메시지
    private lateinit var emailErrorText2: TextView   // 중복 이메일 에러 메시지
    private lateinit var nextButton: AppCompatButton

    // [테스트용] 이미 사용중인 이메일 리스트
    private val usedEmails = listOf("test@planup.com", "user@gmail.com")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        val backIcon = view.findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // 이전 Fragment로 돌아가기
        }


        emailEditText = view.findViewById(R.id.emailEditText)
        emailErrorText1 = view.findViewById(R.id.emailErrorText1)
        emailErrorText2 = view.findViewById(R.id.emailErrorText2)
        nextButton = view.findViewById(R.id.nextButton)

        // 처음엔 에러 메시지 숨김
        emailErrorText1.visibility = View.GONE
        emailErrorText2.visibility = View.GONE

        // 처음엔 버튼 비활성화 상태 유지
        disableNextButton()

        /* 이메일 입력 변화 감지 → 실시간 검증 */
        emailEditText.addTextChangedListener {
            val email = it.toString().trim()

            // (1) 이메일 형식 확인
            val isValidFormat = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (!isValidFormat) {
                showEmailFormatError()  // 형식 에러 표시
                disableNextButton()
                return@addTextChangedListener
            }

            // (2) 이미 사용중인 이메일인지 확인
            if (usedEmails.contains(email)) {
                showEmailUsedError()   // 중복 이메일 에러 표시
                disableNextButton()
                return@addTextChangedListener
            }

            // (3) 정상 입력 → 에러 숨기고 버튼 활성화
            hideAllErrors()
            enableNextButton()
        }

        /* 다음 버튼 클릭 → LoginPasswordFragment로 이동 */
        nextButton.setOnClickListener {
            if (nextButton.isEnabled) {  // 활성화된 경우만 이동 가능
                openNextStep()
            }
        }
    }


    /* LoginPasswordFragment로 이동하는 메서드 */
    private fun openNextStep() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, LoginPasswordFragment())  // 다음 단계로 이동
            .addToBackStack(null)  // 뒤로가기 가능
            .commit()
    }


    /* 이메일 형식 에러 표시 */
    private fun showEmailFormatError() {
        emailErrorText1.visibility = View.VISIBLE   // 형식 에러 보여줌
        emailErrorText2.visibility = View.GONE      // 중복 에러는 숨김
    }

    /* 이미 사용 중인 이메일 에러 표시 */
    private fun showEmailUsedError() {
        emailErrorText1.visibility = View.GONE      // 형식 에러 숨김
        emailErrorText2.visibility = View.VISIBLE   // 중복 에러 보여줌
    }


    private fun hideAllErrors() {
        emailErrorText1.visibility = View.GONE
        emailErrorText2.visibility = View.GONE
    }


    /* 다음 버튼 활성 ↔ 비활성 */
    private fun enableNextButton() {  // 다음 버튼 활성화
        nextButton.isEnabled = true
        nextButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.blue_200)
    }


    private fun disableNextButton() {  // 다음 버튼 비활성화
        nextButton.isEnabled = false
        nextButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.black_200)
    }
}
