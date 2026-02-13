package com.planup.planup.main.goal.item

data class DateMemoResponse (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DateMemoResult
)

data class DateMemoResult (
    val memo: String,
    val memoDate: String,
    val exists: Boolean
)

data class MemoRequest(
    val memo: String,
    val memoDate: String,
    val empty: Boolean,
    val trimmedMemo: String
)

data class PostMemoResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PostMemoResult
)

data class PostMemoResult(
    val action: String,
    val memoId: Int,
    val memo: String,
    val memoDate: String,
    val message: String
)