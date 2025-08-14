package com.example.planup.network.adapter

import com.example.planup.network.data.BlockedFriends

interface FriendsBlockedAdapter {
    fun successBlockedFriends(blockedFriendsList: List<BlockedFriends>?)
    fun failBlockedFriends(code: String?, message: String?)
}