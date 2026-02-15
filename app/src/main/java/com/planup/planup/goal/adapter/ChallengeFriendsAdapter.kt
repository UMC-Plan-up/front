package com.planup.planup.goal.adapter

import com.planup.planup.network.data.ChallengeFriends

interface ChallengeFriendsAdapter {
    fun successFriends(friends: List<ChallengeFriends>)
    fun failFriends(response: String)
}