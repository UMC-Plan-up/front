package com.example.planup.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.signup.ui.AgreementFragment

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.signup_container, AgreementFragment())
                .commit()
        }
    }

    // Fragment 이동 공용 메서드
    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, fragment)
            .addToBackStack(null) // 뒤로가기 가능
            .commit()
    }
}
