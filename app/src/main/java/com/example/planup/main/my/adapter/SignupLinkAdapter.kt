package com.example.planup.main.my.adapter

interface SignupLinkAdapter {
    fun successEmailSend(email: String)
    fun failEmailSend(message: String)
}