package com.example.planup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.resetPasswordContainer, FindPasswordFragment())
                .commit()
        }
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.resetPasswordContainer, fragment)
            .addToBackStack(null)  // 뒤로가기 가능
            .commit()
    }
}
