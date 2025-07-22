package com.example.planup

import android.content.Intent
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.LinearLayout
import android.util.Patterns
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment

class FindPasswordFragment : Fragment(R.layout.fragment_find_password) {

    private lateinit var emailEditText: EditText
    private lateinit var emailFormatErrorText: TextView
    private lateinit var emailNotFoundErrorText: TextView
    private lateinit var sendVerificationButton: AppCompatButton
    private lateinit var emailDropdownIcon: ImageView

    /* [테스트용] 가입된 이메일 */
    private val registeredEmails = listOf("test@planup.com", "user@gmail.com")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        emailEditText = view.findViewById(R.id.emailEditText)
        emailFormatErrorText = view.findViewById(R.id.emailFormatErrorText)
        emailNotFoundErrorText = view.findViewById(R.id.emailNotFoundErrorText)
        sendVerificationButton = view.findViewById(R.id.sendVerificationButton)
        emailDropdownIcon = view.findViewById(R.id.emailDropdownIcon)

        val backIcon = view.findViewById<ImageView>(R.id.backIcon)

        // 처음엔 버튼 비활성화 & 에러 숨김
        disableNextButton()
        hideAllErrors()

        /* 이메일 입력 변화 감지 → 실시간 검증 */
        emailEditText.addTextChangedListener {
            val email = it.toString().trim()
            validateEmail(email)
        }

        /* 다음 버튼 클릭 → FindPasswordEmailSentFragment로 이동 */
        sendVerificationButton.setOnClickListener {
            if (sendVerificationButton.isEnabled) {
                (requireActivity() as ResetPasswordActivity)
                    .navigateToFragment(FindPasswordEmailSentFragment())
            }
        }

        /* 뒤로가기 아이콘 → LoginActivity로 이동 */
        backIcon.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


        // 이메일 도메인 드롭다운 → PopupWindow 열기
        emailDropdownIcon.setOnClickListener {
            showEmailDomainPopup()
        }
    }

    /* 이메일 형식 + 등록 여부 체크 */
    private fun validateEmail(email: String) {
        val isFormatValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (!isFormatValid) {
            // 이메일 형식 틀림
            showEmailFormatError()
            disableNextButton()
            return
        }

        if (!registeredEmails.contains(email)) {
            // 형식은 맞지만 등록되지 않은 이메일
            showEmailNotFoundError()
            disableNextButton()
            return
        }

        // 정상 이메일 → 버튼 활성화
        hideAllErrors()
        enableNextButton()
    }

    private fun showEmailFormatError() {
        emailFormatErrorText.visibility = View.VISIBLE
        emailNotFoundErrorText.visibility = View.GONE
    }

    private fun showEmailNotFoundError() {
        emailFormatErrorText.visibility = View.GONE
        emailNotFoundErrorText.visibility = View.VISIBLE
    }

    private fun hideAllErrors() {
        emailFormatErrorText.visibility = View.GONE
        emailNotFoundErrorText.visibility = View.GONE
    }

    private fun enableNextButton() {
        sendVerificationButton.isEnabled = true
        sendVerificationButton.backgroundTintList =
            requireContext().getColorStateList(R.color.blue_200)
    }

    private fun disableNextButton() {
        sendVerificationButton.isEnabled = false
        sendVerificationButton.backgroundTintList =
            requireContext().getColorStateList(R.color.black_200)
    }

    /* FindPasswordEmailSentFragment로 이동하는 메서드 */
    private fun openNextStep() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.resetPasswordContainer, FindPasswordEmailSentFragment())
            .addToBackStack(null)
            .commit()
    }


    /* popup 띄우기 */
    private fun showEmailDomainPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_email, null)


        popupView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupWidth = popupView.measuredWidth

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        // 도메인 TextView 가져오기
        val domainGmail = popupView.findViewById<TextView>(R.id.domainGmail)
        val domainNaver = popupView.findViewById<TextView>(R.id.domainNaver)
        val domainKakao = popupView.findViewById<TextView>(R.id.domainKakao)

        // 공통 함수: 도메인 추가 후 popup 닫기
        val addDomain: (String) -> Unit = { domain ->
            val currentText = emailEditText.text.toString()
            val updatedText = if (currentText.contains("@")) {
                currentText.substringBefore("@") + "@$domain"
            } else {
                "$currentText@$domain"
            }
            emailEditText.setText(updatedText)
            emailEditText.setSelection(updatedText.length)
            popupWindow.dismiss() // 선택 후 닫기
        }

        // 각 도메인 클릭 리스너
        domainGmail.setOnClickListener { addDomain("gmail.com") }
        domainNaver.setOnClickListener { addDomain("naver.com") }
        domainKakao.setOnClickListener { addDomain("kakao.com") }

        // PopupWindow 속성
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 8f


        val offsetX = emailEditText.width - popupWidth
        popupWindow.showAsDropDown(emailEditText, offsetX, 0)
    }
}
