package com.example.planup.signup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.ui.AgreementFragment
import com.example.planup.signup.ui.InviteCodeInputFragment
import com.example.planup.signup.ui.ProfileSetupFragment
import kotlinx.coroutines.launch

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

        val handled = handleEmailDeepLink(intent)
        if (handled) return

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleEmailDeepLink(intent)
    }


    private fun handleEmailDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        if (uri.scheme != "planup") return false
        if (uri.host != "profile") return false
        if (!uri.path.orEmpty().startsWith("/setup")) return false
        if (deepLinkHandled) return true

        val emailParam = uri.getQueryParameter("email").orEmpty()
        val verifiedParam = uri.getQueryParameter("verified")?.equals("true", true) == true
        val tokenParam = uri.getQueryParameter("token").orEmpty()
        val fromParam = uri.getQueryParameter("from").orEmpty()

        Log.d(
            "EmailDeepLink",
            "uri=$uri, email=$emailParam, verified=$verifiedParam, token=$tokenParam, from=$fromParam"
        )

        deepLinkHandled = true

        if (tokenParam.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    val res = RetrofitInstance.userApi.verifyEmailLink(tokenParam)
                    val body = res.body()
                    val ok = res.isSuccessful &&
                            body?.isSuccess == true &&
                            body?.result?.verified == true

                    if (ok) {
                        goToProfileSetup(emailParam)
                    } else {
                        Toast.makeText(
                            this@SignupActivity,
                            body?.message ?: "이메일 인증 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                        deepLinkHandled = false
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SignupActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    deepLinkHandled = false
                }
            }
            return true
        }

        if (verifiedParam) {
            goToProfileSetup(emailParam)
            return true
        } else {
            Toast.makeText(this, "이메일 인증이 완료되지 않았어요.", Toast.LENGTH_SHORT).show()
            deepLinkHandled = false
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
