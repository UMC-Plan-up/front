package com.example.planup.network.adapter

interface FriendReportAdapter {
    fun successReportFriend()
    fun failReportFriend(code: String, message: String)
}