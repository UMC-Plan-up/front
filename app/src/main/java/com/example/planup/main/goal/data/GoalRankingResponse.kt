package com.example.planup.main.goal.data

import com.example.planup.main.goal.item.TimerVerification

data class GoalRankingResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: RankingList
)

data class RankingList(
    val goalRankingList: List<GoalRanking>
)

data class GoalRanking(
    val goalId: Int,
    val userId: Int,
    val nickName: String,
    val profileImg: String,
    val verificationCount: Int // 인증 한 횟수
)