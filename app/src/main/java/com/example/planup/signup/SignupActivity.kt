package com.example.planup.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.signup.ui.AgreementFragment
import com.example.planup.signup.ui.InviteCodeInputFragment
import com.example.planup.signup.ui.ProfileSetupFragment

class SignupActivity : AppCompatActivity() {

    var email: String? = null
    var password: String? = null
    var nickname: String? = null
    var profileImgUrl: String? = null
    var inviteCode: String? = null
    var agreements: List<Agreement>? = null

    private var deepLinkHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // 1) 커스텀 딥링크 우선 처리
        val handled = handleEmailDeepLink(intent)
        if (handled) return

        // 2) 딥링크가 없으면 기존 초기 플로우
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
                supportFragmentManager.beginTransaction()
                    .replace(R.id.signup_container, AgreementFragment())
                    .commit()
            }
        }
    }

    // singleTop에서 앱이 떠있는 상태로 딥링크 수신 시 호출
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleEmailDeepLink(intent)
    }

    /*
     * 최종 URL: planup://profile/setup?email=...&verified=true&token=...&from=email_verification
     *
     *  @return true = 딥링크 처리하여 다음 화면으로 이동함
     */
    private fun handleEmailDeepLink(intent: Intent): Boolean {
        if (deepLinkHandled) return false

        val uri = intent.data ?: return false
        if (uri.scheme != "planup") return false
        if (uri.host != "profile") return false
        if (!uri.path.orEmpty().startsWith("/setup")) return false

        val emailParam = uri.getQueryParameter("email").orEmpty()
        val verifiedParam = uri.getQueryParameter("verified")?.equals("true", true) == true
        val tokenParam = uri.getQueryParameter("token").orEmpty()
        val fromParam = uri.getQueryParameter("from").orEmpty()

        Log.d("EmailDeepLink",
            "uri=$uri, email=$emailParam, verified=$verifiedParam, token=$tokenParam, from=$fromParam"
        )

        deepLinkHandled = true

        if (verifiedParam) {
            goToProfileSetup(emailParam)
            return true
        } else {
            Toast.makeText(this, "이메일 인증이 완료되지 않았어요.", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    /* 이메일 인증 완료 후 → 프로필 설정 화면으로 이동 */
    private fun goToProfileSetup(emailParam: String) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.signup_container,
                ProfileSetupFragment().apply {
                    arguments = Bundle().apply { putString("email", emailParam) }
                }
            )
            .addToBackStack(null)
            .commit()
    }

    data class Agreement(
        val termsId: Int,
        val isAgreed: Boolean
    )

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
