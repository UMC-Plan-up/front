package com.planup.planup.network.dto.notification

import com.google.gson.annotations.SerializedName
import com.planup.planup.BuildConfig

/**
 * 파이어베이스 토큰을 등록하거나 갱신하는 요청
 * 별도 요청이 없는 경우 token 만 파라미터에 넣어서 사용
 *
 * @property token 파이어베이스 토큰
 * @property platform 요청 플랫폼, ANDROID 고정으로 사용
 * @property appVersion 앱 버전, 앱 버전명을 사용
 * @property local 지역명, ko-KR 고정으로 사용
 */
data class UpdateDeviceTokenRequest(
    @SerializedName("token") val token: String,
    @SerializedName("platform") val platform: String = "ANDROID",
    @SerializedName("appVersion") val appVersion: String = BuildConfig.VERSION_NAME,
    @SerializedName("local") val local: String = "ko-KR"
)

