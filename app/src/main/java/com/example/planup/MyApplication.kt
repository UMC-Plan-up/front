package com.example.planup

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import java.security.MessageDigest

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)


    }


}
