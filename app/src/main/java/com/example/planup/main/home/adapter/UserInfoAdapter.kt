package com.example.planup.main.home.adapter

import com.example.planup.network.data.UserInfo

interface UserInfoAdapter {
    fun successUserInfo(user: UserInfo)
    fun failUserInfo(message: String)
}