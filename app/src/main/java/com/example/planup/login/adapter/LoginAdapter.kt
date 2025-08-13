package com.example.planup.login.adapter

import com.example.planup.network.data.Login

interface LoginAdapter {
    fun successLogin(loginResult: Login)
    fun failLogin(message: String)
}