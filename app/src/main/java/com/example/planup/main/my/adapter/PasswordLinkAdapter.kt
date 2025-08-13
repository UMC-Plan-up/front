package com.example.planup.main.my.adapter

interface PasswordLinkAdapter {
    fun successPasswordLink(email: String)
    fun failPasswordLink(message: String)
}