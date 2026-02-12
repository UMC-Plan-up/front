package com.planup.planup.signup.data

data class InviteCodeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: InviteCodeResult
)

data class InviteCodeResult(
    val inviteCode: String
)
