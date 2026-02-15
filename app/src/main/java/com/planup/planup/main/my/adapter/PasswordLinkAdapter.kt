package com.planup.planup.main.my.adapter

interface PasswordLinkAdapter {
    fun successPasswordLink(token: String)
    fun failPasswordLink(message: String)
}