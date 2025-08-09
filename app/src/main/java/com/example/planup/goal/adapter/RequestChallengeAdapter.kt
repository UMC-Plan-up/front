package com.example.planup.goal.adapter

interface RequestChallengeAdapter {
    fun successRequest()
    fun failRequest(response: String)
}