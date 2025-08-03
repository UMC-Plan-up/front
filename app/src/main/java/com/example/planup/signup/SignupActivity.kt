package com.example.planup.signup

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.signup.ui.AgreementFragment

class SignupActivity : AppCompatActivity() {

    var email: String? = null
    var password: String? = null
    var nickname: String? = null
    var profileImgUrl: String? = null
    var inviteCode: String? = null
    var agreements: List<Agreement>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.signup_container, AgreementFragment())
                .commit()
        }
    }

    data class Agreement(
        val termsId: Int,
        val isAgreed: Boolean
    )

    // Fragment 이동 공용 메서드
    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
