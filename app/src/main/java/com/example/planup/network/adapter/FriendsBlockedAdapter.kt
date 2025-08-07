package com.example.planup.network.adapter

import com.example.planup.network.data.BlockedFriend

interface FriendsBlockedAdapter {
    fun successBlockedFriends(blockedFriendsList: List<BlockedFriend>?)
    fun failBlockedFriends(code: String?, message: String?)
}