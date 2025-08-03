
package com.example.planup.login.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.password.ResetPasswordActivity
import com.example.planup.signup.SignupActivity
import kotlinx.coroutines.launch

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

        window.decorView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        view.clearFocus()
                        hideKeyboard()
                    }
                }
                v.performClick()
            }
            true
        }
    }

        private fun initView() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupButton)
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
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggleIcon.setImageResource(R.drawable.ic_eye_on)
            } else {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggleIcon.setImageResource(R.drawable.ic_eye_off)
            }

            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }

        // 이메일 드롭다운 클릭 시 PopupWindow 열기
        emailDropdownIcon.setOnClickListener {
            showEmailDomainPopup()
        }
    }

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

        val domainGmail = popupView.findViewById<TextView>(R.id.domainGmail)
        val domainNaver = popupView.findViewById<TextView>(R.id.domainNaver)
        val domainKakao = popupView.findViewById<TextView>(R.id.domainKakao)

        val addDomain: (String) -> Unit = { domain ->
            val currentText = emailEditText.text.toString()
            val updatedText = if (currentText.contains("@")) {
                currentText.substringBefore("@") + "@$domain"
            } else {
                "$currentText@$domain"
            }
            emailEditText.setText(updatedText)
            emailEditText.setSelection(updatedText.length)
            popupWindow.dismiss()
        }

        domainGmail.setOnClickListener { addDomain("gmail.com") }
        domainNaver.setOnClickListener { addDomain("naver.com") }
        domainKakao.setOnClickListener { addDomain("kakao.com") }

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 8f

        val offsetX = emailEditText.width - popupWidth
        popupWindow.showAsDropDown(emailEditText, offsetX, 0)
    }

    private fun fadeInView(view: View) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate().alpha(1f).setDuration(300).start()
        view.postDelayed({ fadeOutView(view) }, 2000)
    }

    private fun fadeOutView(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction { view.visibility = View.GONE }
            .start()
    }

    private fun loginWithServer(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val request = com.example.planup.login.data.LoginRequestDto(email, password)
                val response = com.example.planup.network.RetrofitInstance.loginApi.login(request)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val result = response.body()!!.result

                    val prefs = applicationContext.getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    prefs.edit()
                        .putString("accessToken", result.accessToken)
                        .putString("nickname", result.nickname)
                        .putString("profileImgUrl", result.profileImgUrl)
                        .commit()

                    // 저장 후 확인 로그
                    val savedToken = prefs.getString("accessToken", null)
                    Log.d("Login", "저장된 accessToken: $savedToken")


                    // 로그인 성공 → MainActivity로 이동
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    val code = response.body()?.code ?: "UNKNOWN"
                    when (code) {
                        "S001" -> fadeInView(emailFormatErrorText)
                        "U001" -> fadeInView(emailNotFoundErrorText)
                        "S002" -> {
                            fadeInView(emailNotFoundErrorText)
                            fadeInView(passwordNotFoundErrorText)
                        }
                        else -> fadeInView(passwordNotFoundErrorText)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // 에러 메시지 숨기기
        emailFormatErrorText.visibility = View.GONE
        emailNotFoundErrorText.visibility = View.GONE
        passwordNotFoundErrorText.visibility = View.GONE

        var hasError = false

        // 이메일 형식 검증
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            fadeInView(emailFormatErrorText)
            hasError = true
        }

        if (email.isEmpty()) {
            fadeInView(emailNotFoundErrorText)
            hasError = true
        }

        if (password.isEmpty()) {
            fadeInView(passwordNotFoundErrorText)
            hasError = true
        }

        // 오류 없으면 서버에 로그인 요청
        if (!hasError) {
            loginWithServer(email, password)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
