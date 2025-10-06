package com.example.planup.network

import android.app.Application
import com.example.planup.R
import com.example.planup.database.TokenSaver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var jwt: TokenManager
    }

    @Inject
    lateinit var tokenSaver: TokenSaver

    override fun onCreate() {
        super.onCreate()
        jwt = TokenManager(tokenSaver)

        com.kakao.sdk.common.KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}