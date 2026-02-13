package com.planup.planup.main.goal.item

data class GetCommentsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<GetCommentsResult>
)

data class GetCommentsResult(
    val id: Int,
    val content: String,
    val writerId: Int,
    val writerNickname: String,
    val writerProfileImg: String,
    val parentCommentId: Int,
    val parentCommentContent: String,
    val parentCommentWriter: String,
    val reply: Boolean,
    val myComment: Boolean
)

data class CreateCommentRequest(
    val content: String,
    val parentCommentId: Int,
    val reply: Boolean
)

data class CreateCommentResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: CreateCommentResult
)
data class CreateCommentResult(
    val id: Int,
    val content: String,
    val writerId: Int,
    val writerNickname: String,
    val writerProfileImg: String,
    val parentCommentId: Int,
    val parentCommentContent: String,
    val parentCommentWriter: String,
    val reply: Boolean,
    val myComment: Boolean
)