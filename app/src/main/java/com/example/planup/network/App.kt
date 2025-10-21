package com.example.planup.network

import android.app.Application
import com.example.planup.R
import com.example.planup.database.TokenSaver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    companion object {
        @Deprecated(message = "TokenSaver 사용")
        lateinit var jwt: TokenManager
    }

    @Inject
    lateinit var tokenSaver: TokenSaver

    override fun onCreate() {
        super.onCreate()
        jwt = TokenManager(tokenSaver)

        // TODO:: 상수화
        com.kakao.sdk.common.KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}