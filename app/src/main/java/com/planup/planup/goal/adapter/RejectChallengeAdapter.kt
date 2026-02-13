package com.planup.planup.goal.adapter

interface RejectChallengeAdapter {
    fun successReject() //거절 성공
    fun failReject(message: String) //오류
}