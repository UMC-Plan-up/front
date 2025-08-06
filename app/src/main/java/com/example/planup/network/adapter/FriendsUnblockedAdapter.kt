package com.example.planup.network.adapter

interface FriendsUnblockedAdapter {
    fun successFriendUnblock(name: String)
    fun failFriendUnblock(code: String, message: String)
}