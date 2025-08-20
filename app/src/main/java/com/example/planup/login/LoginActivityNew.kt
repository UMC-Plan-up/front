package com.example.planup.login

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.ActivityLoginBinding
import com.example.planup.login.adapter.LoginAdapter
import com.example.planup.main.home.adapter.UserInfoAdapter
import com.example.planup.network.App
import com.example.planup.network.RetrofitInstance
import com.example.planup.network.controller.UserController
import com.example.planup.network.data.Login
import com.example.planup.network.data.UserInfo
import com.example.planup.network.dto.user.LoginDto
import com.example.planup.password.ResetPasswordActivity
import com.example.planup.signup.SignupActivity
import com.example.planup.signup.data.KakaoLoginRequest
import com.kakao.sdk.auth.AuthCodeClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LoginActivityNew: AppCompatActivity(), LoginAdapter, UserInfoAdapter {
    lateinit var binding: ActivityLoginBinding

    lateinit var prefs: SharedPreferences //로그인한 유저 닉네임, 프로필 사진 저장
    lateinit var editor: Editor //로그인한 유저 닉네임, 프로필 사진 저장

    lateinit var service: UserController //API 연동
    var isPwVisible: Boolean = false //비밀번호 가리기 설정 버튼

    /* 화면 터치 시 EditText 밖을 누르면 키보드 숨기기 */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) { // 현재 포커스가 EditText일 경우만
                    val outRect = android.graphics.Rect()
                    view.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                        view.clearFocus()
                        hideKeyboard(view) // 키보드 숨김
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 혹시 dispatchTouchEvent에서 놓치는 경우 보완
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) {
                    view.clearFocus()
                    hideKeyboard(view)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        clickListener()
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
        service.setLoginAdapter(this)
    }

    private fun clickListener() {
        // 로그인 버튼
        binding.loginButton.setOnClickListener { checkLogin() }

        // 회원가입 화면 전환
        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 화면 전환
        binding.forgotPasswordText.setOnClickListener {
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
            onClickKakaoLogin()
        }
    }

    private fun checkLogin() {
        //이메일 형식이 옳바르지 않은 경우
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text.toString()).matches()) {
            makeToast(R.string.toast_incorrect_email)
            return
        } else { //이메일 형식이 옳바른 경우 서버에 로그인 요청
            service.loginService(
                LoginDto(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
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
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 700)
        toast.show()
    }

    private fun makeToast(message: String) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(this)
        toast.view = layout
        toast.duration = LENGTH_SHORT
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

    //로그인 통신 성공
    override fun successLogin(loginResult: Login) {
        when (loginResult.message) {
            "존재하지 않는 사용자입니다" -> makeToast("등록되지 않은 이메일이에요")
            "비밀번호가 일치하지 않습니다" -> makeToast("비밀번호를 다시 확인해 주세요.")
            else -> {
                editor.putString("accessToken", loginResult.accessToken)
                editor.apply() // 즉시 저장
                App.jwt.token = "Bearer " + loginResult.accessToken
                // 토큰 및 사용자 정보 저장 로직을 통합 함수로 처리
                service.setUserInfoAdapter(this)
                service.userInfoService()
            }
        }
    }

    //로그인 통신 실패 -> 토스트 메시지 출력
    override fun failLogin(message: String) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(this)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }

    //유저 정보 요청 통신 성공
    override fun successUserInfo(user: UserInfo) {
        // 유저 정보와 토큰을 함께 저장하고 메인으로 이동하는 통합 함수 호출
        saveUserInfoAndGoToMain(
            user.id,
            user.email,
            user.nickname,
            user.profileImage
        )
    }

    //유저 정보 요청 통신 실패 -> 토스트 메시지 출력
    override fun failUserInfo(message: String) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(this)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, LENGTH_SHORT).show()
    }

    // 카카오 인가코드 얻기
    private suspend fun getKakaoAuthorizationCode(): String =
        suspendCancellableCoroutine { cont ->
            val callback: (String?, Throwable?) -> Unit = { code, error ->
                if (error != null) cont.resumeWithException(error)
                else cont.resume(code ?: "")
            }

            val client = AuthCodeClient.instance
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
                        App.jwt.token = r.accessToken
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
        startActivity(Intent(this, com.example.planup.main.MainActivity::class.java))
        finish()
    }
}
