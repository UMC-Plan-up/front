package com.example.planup.network

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.example.planup.BuildConfig
import com.example.planup.database.TokenSaver
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import java.security.MessageDigest
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

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        printKeyHash()
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }

    private fun printKeyHash() {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            signatures?.forEach { signature ->
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash", "keyHash: $keyHash")
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "키 해시 얻기 실패", e)
        }
    }
}