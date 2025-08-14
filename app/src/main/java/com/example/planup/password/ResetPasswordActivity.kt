package com.example.planup.password

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.ActivityResetPasswordBinding
import com.example.planup.password.ui.FindPasswordFragment

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

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