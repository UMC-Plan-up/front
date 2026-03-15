package com.planup.planup.main.home.data

import android.os.Parcelable

sealed class ChallengeReceived(): Parcelable{
    abstract  val userId: Int //사용자 id
    abstract  val challengeId: Int //챌린지 id
    abstract  val friendId: List<Long> //챌린지를 요청한 친구의 id
    abstract  val friendName: String //친구 이름
    abstract  val goalName: String //목표명
    abstract  val goalAmount: String //1회 분량
    abstract  val endDate: String //종료일
    abstract  val period: String //기준 기간
    abstract  val frequency: String //빈도
    abstract  val penalty: String //페널티
}