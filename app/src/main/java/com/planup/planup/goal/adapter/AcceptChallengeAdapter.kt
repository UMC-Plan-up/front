package com.planup.planup.goal.adapter

interface AcceptChallengeAdapter {
    fun successAccept() //수락 성공
    fun failAccept(message: String) //오류
}