package com.example.planup.network

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.example.planup.BuildConfig
import com.example.planup.database.TokenSaver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() , SingletonImageLoader.Factory {

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
        com.kakao.sdk.common.KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }

}