package com.planup.planup.network.dto.goal

data class ReactionResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ReactionResultDto
)
data class ReactionResultDto(
    val result: String,
    val message: String,
    val reactionData: ReactionDataDto
)
data class ReactionDataDto(
    val goalId: Long,
    val cheerCount: Int,
    val encourageCount: Int,
    val userReactions: UserReactionsDto
)

data class UserReactionsDto(
    val hasCheer: Boolean,
    val hasEncourage: Boolean
)

data class CommunityReportRequest(
    val reason: String
)

data class CommunityComplaintsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Unit
)

