package com.example.planup.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.password.ResetPasswordActivity
import com.example.planup.signup.SignupActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView
    private lateinit var forgotPasswordText: TextView

    private lateinit var emailFormatErrorText: TextView
    private lateinit var emailNotFoundErrorText: TextView
    private lateinit var passwordNotFoundErrorText: TextView

    // 비밀번호 보이기/숨기기
    private lateinit var passwordToggleIcon: ImageView
    private var isPwVisible = false  // 현재 비밀번호 표시 상태 저장

    // 이메일 도메인 드롭다운 아이콘
    private lateinit var emailDropdownIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        initClickListener()
    }

    private fun initView() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)

        emailFormatErrorText = findViewById(R.id.emailFormatErrorText)
        emailNotFoundErrorText = findViewById(R.id.emailNotFoundErrorText)
        passwordNotFoundErrorText = findViewById(R.id.passwordNotFoundErrorText)

        passwordToggleIcon = findViewById(R.id.passwordToggleIcon)
        emailDropdownIcon = findViewById(R.id.emailDropdownIcon)

        emailFormatErrorText.visibility = View.GONE
        emailNotFoundErrorText.visibility = View.GONE
        passwordNotFoundErrorText.visibility = View.GONE
    }

    private fun initClickListener() {
        // 로그인 버튼
        loginButton.setOnClickListener { checkLogin() }

        // 회원가입 화면 전환
        signupText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 화면 전환
        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        // 눈 아이콘 클릭 시 비밀번호 보이기/숨기기
        passwordToggleIcon.setOnClickListener {
            isPwVisible = !isPwVisible // 상태 반전

            if (isPwVisible) {
                // 비밀번호 보이도록 변경
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggleIcon.setImageResource(R.drawable.ic_eye_on) // 눈 icon으로 변경
            } else {
                // 비밀번호 숨기도록 변경
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggleIcon.setImageResource(R.drawable.ic_eye_off) // 눈+작대기 icon으로 변경
            }

            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }

        // 이메일 드롭다운 클릭 시 PopupWindow 열기
        emailDropdownIcon.setOnClickListener {
            showEmailDomainPopup()
        }
    }

    // PopupWindow로 이메일 도메인 선택 UI 띄우기
    private fun showEmailDomainPopup() {

        val popupView = layoutInflater.inflate(R.layout.popup_email, null)

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

        // 각 도메인 클릭 리스너 (선택 후 popup 닫기만)
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

    private fun fadeInView(view: View) {
        view.alpha = 0f              // 처음엔 투명하게
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)               // 점점 보이게
            .setDuration(300)        // 0.3초 동안 애니메이션
            .start()

        // 2초 후 자동으로 사라지게
        view.postDelayed({
            fadeOutView(view)
        }, 2000)
    }

    private fun fadeOutView(view: View) {
        view.animate()
            .alpha(0f)               // 점점 투명하게
            .setDuration(300)        // 0.3초 동안 애니메이션
            .withEndAction {
                view.visibility = View.GONE
            }
            .start()
    }

    private fun checkLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        emailFormatErrorText.visibility = View.GONE
        emailNotFoundErrorText.visibility = View.GONE
        passwordNotFoundErrorText.visibility = View.GONE

        var hasError = false

        // 이메일 형식 검증
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            fadeInView(emailFormatErrorText)
            hasError = true
        }

        // [테스트용] 회원가입 완료 된 계정
        val dummyEmail = "test@test.com"
        val dummyPassword = "1234"

        if (email.isNotEmpty() && email != dummyEmail) {
            fadeInView(emailNotFoundErrorText)
            hasError = true
        }

        if (password.isNotEmpty() && password != dummyPassword) {
            fadeInView(passwordNotFoundErrorText)
            hasError = true
        }

        // 로그인 성공 → MainActivity로 이동
        if (!hasError) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
