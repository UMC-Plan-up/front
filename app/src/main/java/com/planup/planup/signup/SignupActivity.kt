package com.planup.planup.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.planup.planup.R
import com.planup.planup.signup.data.SignUpDraftStore
import com.planup.planup.signup.ui.*
import com.kakao.sdk.user.UserApiClient

class SignupActivity : AppCompatActivity() {

    var email: String? = null
    var password: String? = null
    var nickname: String? = null
    var profileImgUrl: String? = null
    var inviteCode: String? = null
    var agreements: List<Agreement>? = null
    var tempUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SignupActivity", "onCreate: intent=$intent data=${intent?.data}")
        setContentView(R.layout.activity_signup)


        // 1) 딥링크를 최우선으로 처리
        val handled = handleEmailDeepLink(intent)
        if (handled) return

        // 2) 카카오 소셜 로그인 플로우 처리
        val provider = intent.getStringExtra("provider")
        if (provider == "KAKAO") {
            this.email = intent.getStringExtra("email")
            this.profileImgUrl = intent.getStringExtra("profileImg")
            this.tempUserId = intent.getStringExtra("tempUserId")
            this.password = "social_login_password_placeholder"

            if (this.email.isNullOrBlank()) {
                // 이메일이 없으면 추가 동의 요청
                val scopes = listOf("account_email")
                UserApiClient.instance.loginWithNewScopes(this, scopes) { token, error ->
                    if (error != null) {
                        Log.e("SignupActivity", "이메일 추가 동의 실패", error)
                        openAgreementForKakao()
                    } else {
                        // 추가 동의 후 사용자 정보 다시 요청
                        UserApiClient.instance.me { user, error2 ->
                            if (error2 != null) {
                                Log.e("SignupActivity", "동의 후 사용자 정보 요청 실패", error2)
                            } else {
                                this.email = user?.kakaoAccount?.email
                                Log.d("SignupActivity", "동의 후 이메일=${this.email}")
                            }
                            openAgreementForKakao()
                        }
                    }
                }
            } else {
                openAgreementForKakao()
            }
            return
        }

        // 3) 일반 회원가입 또는 초대코드 플로우
        if (savedInstanceState == null) {
            val code = intent.getStringExtra("code")
            if (!code.isNullOrBlank()) {
                val fragment = InviteCodeInputFragment().apply {
                    arguments = Bundle().apply { putString("inviteCode", code) }
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, fragment)
                    .commit()
            } else {
                val bundle = Bundle().apply { putBoolean("isKakaoSignup", false) }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, AgreementFragment().apply { arguments = bundle })
                    .commit()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleEmailDeepLink(intent)
    }

    private fun openAgreementForKakao() {
        val bundle = Bundle().apply {
            putString("tempUserId", this@SignupActivity.tempUserId)
            putBoolean("isKakaoSignup", true)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, AgreementFragment().apply { arguments = bundle })
            .commit()
    }


    /*
     * URL: planup://profile/setup?email=...&verified=true&token=...&from=email_verification
     * true = 딥링크 처리하여 화면 전환
     */
    private fun handleEmailDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        Log.d("handleEmailDeepLink", "Received URI: $uri")

        val schemeOk = uri.scheme.equals("planup", true)
        val hostOk = uri.host.equals("profile", true)
        val rawPath = (uri.path ?: "").trimEnd('/')
        val pathOk = rawPath.equals("/setup", true)
        if (!schemeOk || !hostOk || !pathOk) {
            Log.w("handleEmailDeepLink", "Deep link host/path mismatch: $schemeOk, $hostOk, $rawPath")
            return false
        }

        val emailParam = uri.getQueryParameter("email").orEmpty()
        val verifiedStr = uri.getQueryParameter("verified").orEmpty()
        val tokenParam = uri.getQueryParameter("token").orEmpty()

        val verifiedParam = verifiedStr.equals("true", true) || verifiedStr == "1" || verifiedStr.equals("yes", true)

        Log.d("handleEmailDeepLink",
            "email='$emailParam', verifiedStr='$verifiedStr' -> $verifiedParam, token='${tokenParam.take(6)}...'")

        if (!verifiedParam || tokenParam.isBlank()) {
            Toast.makeText(this, "이메일 인증이 완료되지 않았거나 토큰이 유효하지 않습니다.", Toast.LENGTH_SHORT).show()
            Log.e("handleEmailDeepLink", "Verification failed. verifiedParam=$verifiedParam, tokenParam.isBlank=${tokenParam.isBlank()}")
            return true
        }

        this.email = emailParam.ifBlank {
            this.email ?: SignUpDraftStore.loadEmail(this)
        }
        this.password = this.password ?: SignUpDraftStore.loadPw(this)

        openProfileSetup(
            email = this.email,
            verifyToken = tokenParam,
            addToBackStack = true
        )

        Log.d("handleEmailDeepLink", "Successfully opened ProfileSetupFragment.")
        return true
    }


    // ProfileSetupFragment로 이동하는 함수
    fun openProfileSetup(email: String?, verifyToken: String?, addToBackStack: Boolean) {
        val f = ProfileSetupFragment().apply {
            arguments = Bundle().apply {
                putString("email", email)
                putString("verifyToken", verifyToken)
            }
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.signup_container, f)
            if (addToBackStack) addToBackStack(null)
            commit()
        }
    }

    fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.signup_container, fragment)
            if (addToBackStack) addToBackStack(null)
            commit()
        }
    }

    data class Agreement(
        val termsId: Int,
        val isAgreed: Boolean
    )
}
