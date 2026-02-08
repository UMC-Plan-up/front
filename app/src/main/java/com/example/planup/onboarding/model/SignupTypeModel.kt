package com.example.planup.onboarding.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class SignupTypeModel: Parcelable {

    // 일반 이메일 인증 유저
    @Parcelize
    object Normal: SignupTypeModel()

    // 카카오 인증 유저
    @Parcelize
    data class Kakao(
        val tempUserId: String,
        val email: String
    ): SignupTypeModel()
}