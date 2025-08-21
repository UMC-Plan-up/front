package com.example.planup.main.my.adapter

interface PasswordLinkAdapter {
    fun successPasswordLink(token: String)
    fun failPasswordLink(message: String)
}