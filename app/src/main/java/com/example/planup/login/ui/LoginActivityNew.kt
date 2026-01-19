package com.example.planup.login.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.planup.R
import com.example.planup.databinding.ActivityLoginBinding
import com.example.planup.main.MainActivity
import com.example.planup.App
import com.example.planup.network.RetrofitInstance
import com.example.planup.network.controller.UserController
import com.example.planup.password.ResetPasswordActivity
import com.example.planup.signup.SignupActivity
import com.example.planup.signup.data.KakaoLoginRequest
import com.example.planup.util.KakaoServiceHandler
import com.kakao.sdk.auth.AuthCodeClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@AndroidEntryPoint
class LoginActivityNew: AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    lateinit var prefs: SharedPreferences //로그인한 유저 닉네임, 프로필 사진 저장
    lateinit var editor: SharedPreferences.Editor //로그인한 유저 닉네임, 프로필 사진 저장

    lateinit var service: UserController //API 연동
    var isPwVisible: Boolean = false //비밀번호 가리기 설정 버튼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        clickListener()
        initObservers()
        window.decorView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        view.clearFocus()
                        hideKeyboard(view)
                    }
                }
                v.performClick()
            }
            true
        }
    }

    private fun init() {
        prefs = getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = prefs.edit()
        service = UserController()
    }

    private fun clickListener() {
        // 로그인 버튼
        binding.loginButton.setOnClickListener {
            checkLogin()
        }

        // 회원가입 화면 전환
        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 화면 전환
        binding.forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        // 눈 아이콘 클릭 시 비밀번호 보이기/숨기기
        binding.passwordToggleIcon.setOnClickListener {
            isPwVisible = !isPwVisible // 상태 반전

            if (isPwVisible) {
                binding.passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.passwordToggleIcon.setImageResource(R.drawable.ic_eye_on)
            } else {
                binding.passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordToggleIcon.setImageResource(R.drawable.ic_eye_off)
            }

            binding.passwordEditText.setSelection(binding.passwordEditText.text?.length ?: 0)
        }

        // 이메일 드롭다운 클릭 시 PopupWindow 열기
        binding.emailDropdownIcon.setOnClickListener {
            showEmailDomainPopup()
        }

        // 카카오 로그인 버튼
        binding.kakaoLoginLayout.setOnClickListener {
            // TODO:: 뷰모델로 로직 분리
            onClickKakaoLogin()
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventChannel.collect { event ->
                    when(event) {
                        is LoginViewModel.Event.FailLogin -> makeToast(event.message)
                        LoginViewModel.Event.SuccessLogin -> {
                            startActivity(Intent(this@LoginActivityNew, MainActivity::class.java))
                            finish()
                        }
                        LoginViewModel.Event.UnknownEmail -> makeToast("등록되지 않은 이메일이에요")
                        LoginViewModel.Event.UnknownError -> makeToast("알 수 없는 오류가 발생했습니다")
                        LoginViewModel.Event.WrongPassword -> makeToast("비밀번호를 다시 확인해 주세요.")
                    }
                }
            }
        }

    }

    private fun checkLogin() {
        //이메일 형식이 옳바르지 않은 경우
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text.toString()).matches()) {
            makeToast(R.string.toast_incorrect_email)
            return
        } else { //이메일 형식이 옳바른 경우 서버에 로그인 요청
            viewModel.requestLogin(
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString()
            )
        }
    }

    //이메일 입력 시 도메인 자동 입력 드롭다운
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
            val currentText = binding.emailEditText.text.toString()
            val updatedText = if (currentText.contains("@")) {
                currentText.substringBefore("@") + "@$domain"
            } else {
                "$currentText@$domain"
            }
            binding.emailEditText.setText(updatedText)
            binding.emailEditText.setSelection(updatedText.length)
            popupWindow.dismiss()
        }

        domainGmail.setOnClickListener { addDomain("gmail.com") }
        domainNaver.setOnClickListener { addDomain("naver.com") }
        domainKakao.setOnClickListener { addDomain("kakao.com") }

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 8f

        val offsetX = binding.emailEditText.width - popupWidth
        popupWindow.showAsDropDown(binding.emailEditText, offsetX, 0)
    }

    //비밀번호 입력 결과에 따른 토스트메시지
    private fun makeToast(message: Int) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).setText(message)

        val toast = Toast(this)
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 700)
        toast.show()
    }

    private fun makeToast(message: String) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(this)
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 700)
        toast.show()
    }

    //화면 터치 시 키보드 사라지게
    private fun hideKeyboard(view: View?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // 카카오 인가코드 얻기
    private suspend fun getKakaoAuthorizationCode(): String =
        suspendCancellableCoroutine { cont ->
            val callback: (String?, Throwable?) -> Unit = { code, error ->
                println("code: ${code}|\n error: ${error}")
                if (error != null) cont.resumeWithException(error)
                else cont.resume(code ?: "")
            }

            val client = AuthCodeClient.Companion.instance
            if (client.isKakaoTalkLoginAvailable(this@LoginActivityNew)) {
                client.authorizeWithKakaoTalk(this@LoginActivityNew, callback = callback)
            } else {
                client.authorizeWithKakaoAccount(this@LoginActivityNew, callback = callback)
            }
        }

    // 카카오 로그인 실행
    private fun onClickKakaoLogin() {
        lifecycleScope.launch {
            try {
                val code = getKakaoAuthorizationCode()
                Log.d("KakaoLogin", "Received authorization code: $code")
                val resp = RetrofitInstance.userApi.kakaoLogin(KakaoLoginRequest(code))
                val body = resp.body()

                if (resp.isSuccessful && body?.isSuccess == true) {
                    val r = body.result

                    if (r.newUser) {
                        startActivity(
                            Intent(this@LoginActivityNew, SignupActivity::class.java).apply {
                                putExtra("provider", "KAKAO")
                                putExtra("tempUserId", r.tempUserId ?: "")
                                putExtra("email", r.userInfo?.email)
                                putExtra("profileImg", r.userInfo?.profileImg)
                            }
                        )
                    } else {
                        val accessToken = r.accessToken
                        val userInfo = r.userInfo
                        App.Companion.jwt.token = r.accessToken
                        editor.putString("kakaoCode",code)
                        if (accessToken != null && userInfo != null) {
                            saveUserInfoAndGoToMain(userInfo.id.toInt(), userInfo.email, userInfo.nickname, userInfo.profileImg)
                        } else {
                            // API 응답은 성공했지만 필요한 데이터가 누락된 경우
                            Log.e("KakaoLogin", "API call successful but missing accessToken or userInfo.")
                            toast("로그인 처리에 실패했습니다. 잠시 후 다시 시도해주세요.")
                        }
                    }
                } else {
                    // API 호출 실패
                    Log.e("KakaoLogin", "API call failed. Response code: ${resp.code()}, message: ${body?.message}")
                    toast(body?.message ?: "로그인 실패(${resp.code()})")
                }
            } catch (e: Exception) {
                // 카카오 SDK 인증 과정에서 예외가 발생한 경우
                Log.e("KakaoLogin", "Kakao authorization failed: ${e.localizedMessage}", e)
                toast("카카오 인증 실패: ${e.localizedMessage}")
            }
        }
    }

    private fun saveUserInfoAndGoToMain(
        userId: Int,
        email: String?,
        nickname: String?,
        profileImg: String?
    ) {
        editor.putInt("userId", userId)
        editor.putString("email", email)
        editor.putString("nickname", nickname)
        editor.putString("profileImg", profileImg)
        editor.apply()

        // 메인 이동
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}