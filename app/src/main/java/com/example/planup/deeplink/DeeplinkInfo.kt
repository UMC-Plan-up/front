package com.example.planup.deeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.planup.main.MainActivity
import com.example.planup.onboarding.OnBoardingActivity

enum class DeeplinkInfo(val host: String, val path: String) {
    PROFILE_SETUP(host = "profile", path = "/setup") {
        override val needMainForParents: Boolean = false
        override fun getIntent(context: Context, deeplink: Uri): Intent {
            return OnBoardingActivity.getIntent(context, deeplink)
        }
    },

    EMAIL_CHANGE(host = "email", path = "/change") {
        override val needMainForParents: Boolean = false
        override fun getIntent(context: Context, deeplink: Uri): Intent {
            return MainActivity.getIntent(context, deeplink)
        }
    };


    open val needMainForParents = true
    abstract fun getIntent(context: Context, deeplink: Uri): Intent

    companion object {
        fun getMainIntent(context: Context): Intent = MainActivity.getIntent(context)

        // 딥링크 포멧을 알지 못해 Path 까지 고려함
        operator fun invoke(deeplink: Uri): DeeplinkInfo? {
            val host = deeplink.host
            val path = deeplink.path

            if (host == null || path == null) {
                Log.e("DeeplinkInfo", "Deeplink host or path is null: $host, $path")
            }

            // host, path 에 따른 딥링크 연결 정보
            Log.d("DeeplinkInfo", "host: $host, path: $path")
            DeeplinkInfo.entries.forEach { it ->
                if (it.host == host && it.path == path) {
                    return it
                }
            }

            // DeeplinkInfo 에 저장된 딥링크가 아닌 경우 핸들링하지 않음
            Log.e("DeeplinkInfo", "The deeplink does not match any DeeplinkInfo: $host, $path")
            return null
        }
    }
}