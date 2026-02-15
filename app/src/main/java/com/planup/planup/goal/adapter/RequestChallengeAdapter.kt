package com.planup.planup.goal.adapter

interface RequestChallengeAdapter {
    fun successRequest()
    fun failRequest(response: String)
}