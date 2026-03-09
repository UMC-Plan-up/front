package com.planup.planup.main.home.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChallengeReceivedPhoto(
    override val userId: Int, //사용자 id
    override val challengeId: Int, //챌린지 id
    override val friendId: List<Long>, //챌린지를 요청한 친구의 id
    override val friendName: String, //친구 이름
    override val goalAmount: String, //1회 분량
    override val goalName: String, //목표명
    val howMany: Int, //인증 횟수
    override val endDate: String, //종료일
    override val period: String, //기준 기간
    override val frequency: String, //빈도
    override val penalty: String //페널티
): ChallengeReceived()