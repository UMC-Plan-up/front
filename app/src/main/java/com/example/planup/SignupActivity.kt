package com.example.planup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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
