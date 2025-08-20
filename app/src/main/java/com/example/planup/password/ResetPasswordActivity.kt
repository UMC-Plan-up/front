package com.example.planup.password

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.ActivityResetPasswordBinding
import com.example.planup.password.ui.FindPasswordFragment
import com.example.planup.password.ui.ResetPasswordFragment

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) 딥링크 우선 처리
        val handled = handlePasswordResetDeepLink(intent)
        if (handled) return

        // 2) 일반 진입 기본: 이메일 입력 화면
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.resetPasswordContainer, FindPasswordFragment())
                .commit()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePasswordResetDeepLink(intent)
    }

    /**
     * 딥링크 처리 (검증 완료 → 새 비밀번호 화면만)
     * planup://password/change?email=...&verified=true&token=...&from=password_change
     */
    private fun handlePasswordResetDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        Log.d("DeepLinkTest", "Received URI: $uri")

        val validPath = uri.scheme == "planup" &&
                uri.host == "login" &&
                (uri.path ?: "").startsWith("/password/change")

        Log.d("DeepLinkTest", "Path validation result: $validPath")
        if (!validPath) {
            Log.d("DeepLinkTest", "Invalid path. Redirecting to FindPasswordFragment.")
            Toast.makeText(this, "유효하지 않은 또는 만료된 링크입니다.", Toast.LENGTH_SHORT).show()
            supportFragmentManager.beginTransaction()
                .replace(R.id.resetPasswordContainer, FindPasswordFragment())
                .commit()
            return true
        }

        val email = uri.getQueryParameter("email")
        val token = uri.getQueryParameter("token")
        val verified = uri.getQueryParameter("verified")?.equals("true", true) == true
        val from = uri.getQueryParameter("from")

        Log.d("DeepLinkTest", "Email: $email, Token: $token, Verified: $verified, From: $from")

        if (email.isNullOrBlank() || token.isNullOrBlank() || !verified || from != "password_change") {
            Log.d("DeepLinkTest", "Invalid parameters. Redirecting to FindPasswordFragment.")
            Toast.makeText(this, "유효하지 않은 또는 만료된 링크입니다.", Toast.LENGTH_SHORT).show()
            supportFragmentManager.beginTransaction()
                .replace(R.id.resetPasswordContainer, FindPasswordFragment())
                .commit()
            return true
        }

        Log.d("DeepLinkTest", "Valid deep link. Navigating to ResetPasswordFragment.")
        goToResetPassword(email, token)
        return true
    }

    /* 새 비밀번호 설정 화면 */
    private fun goToResetPassword(email: String, token: String) {
        val f = ResetPasswordFragment().apply {
            arguments = Bundle().apply {
                putString("email", email)
                putString("token", token)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.resetPasswordContainer, f)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.resetPasswordContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
