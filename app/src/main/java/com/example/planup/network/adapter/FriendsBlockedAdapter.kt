package com.example.planup.network.adapter

import com.example.planup.network.data.BlockFriendResponse

interface FriendsBlockedAdapter {
    fun successBlockedFriends(blockedFriendsList: List<BlockFriendResponse>?)
    fun failBlockedFriends(code: String?, message: String?)
}