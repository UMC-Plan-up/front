package com.example.planup.deeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.planup.main.MainActivity
import com.example.planup.onboarding.OnBoardingActivity

// planup://profile/setup?email=signuptest1%40test.com&verified=true&token=0db96b11-d499-4dd1-bd1b-06664eba43f5&from=email_verification

enum class DeeplinkInfo(host: String) {
    PROFILE_SETUP(host = "profile") {
        override fun getIntent(context: Context, deeplink: Uri): Intent {
            return OnBoardingActivity.getIntent(context, deeplink)
        }
    };

    abstract fun getIntent(context: Context, deeplink: Uri): Intent

    companion object {
        fun getMainIntent(context: Context): Intent = MainActivity.getIntent(context)

        // 딥링크 포멧을 알지 못해 Path 까지 고려함
        operator fun invoke(deeplink: Uri): DeeplinkInfo? {
            val host = deeplink.host
            val path = deeplink.path

            if (host == null || path == null) {
                Log.e("DeePlinkInfo", "Deeplink host or path is null: $host, $path")
            }

            // host, path 에 따른 딥링크 연결 정보
            return if (host == "profile" && path == "setup")
                PROFILE_SETUP
            else
                null
        }
    }
}