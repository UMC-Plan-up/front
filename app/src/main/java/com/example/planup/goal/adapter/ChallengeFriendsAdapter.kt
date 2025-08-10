package com.example.planup.goal.adapter

import com.example.planup.network.data.challenge.ChallengeFriends

interface ChallengeFriendsAdapter {
    fun successFriends(friends: List<ChallengeFriends>)
    fun failFriends(response: String)
}