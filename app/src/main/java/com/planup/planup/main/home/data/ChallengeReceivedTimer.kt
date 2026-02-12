package com.planup.planup.main.home.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChallengeReceivedTimer(
    val userId: Int, //사용자 id
    val challengeId: Int, //챌린지 id
    val friendId: List<Long>, //챌린지를 요청한 친구의 id
    val friendName: String, //친구 이름
    val goalName: String, //목표명
    val goalAmount: String, //1회 분량
    val targetTime: Int, //타이머 시간
    val endDate: String, //종료일
    val duration: String, //기준 기간
    val frequency: String, //빈도
    val penalty: String //페널티
): Parcelable
