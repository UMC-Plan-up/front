package com.example.planup.network.adapter

import com.example.planup.network.data.BlockedFriend

interface FriendsBlockedAdapter {
    fun successBlockFriend(blockedFriendsList: List<BlockedFriend>?)
    fun failBlockFriend(code: String, message: String)
}