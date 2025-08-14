package com.example.planup.network

import android.app.Application
import com.example.planup.R

class App:Application() {
    companion object {
        lateinit var jwt: TokenManager
    }

    override fun onCreate() {
        jwt = TokenManager(applicationContext)
        super.onCreate()
        com.kakao.sdk.common.KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}