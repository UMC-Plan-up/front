package com.example.planup.network.dto.challenge

import com.google.gson.annotations.SerializedName

data class ChallengeDto(
    @SerializedName("goalName") val goalName: String, //목표명
    @SerializedName("goalAmount") val goalAmount: String, //1회 분량
    @SerializedName("goalType") val goalType: String, //인증 방식
    @SerializedName("endDate") val endDate: String, //종료일
    @SerializedName("status") val status: String, //신청 여부
    @SerializedName("penalty") val penalty: String, //페널티
    @SerializedName("friendId") val friendId: Int, //신청할 친구 id
    @SerializedName("timePerPeriod") val duration: Int, //기준기간
    @SerializedName("frequency") val frequency: Int, //빈도
    @SerializedName("timeChallenge") val timer: Time //타이머 설정 정보
)
data class Time(
    @SerializedName("targetTime") val targetTime: Int //설정한 타이머 시간
)