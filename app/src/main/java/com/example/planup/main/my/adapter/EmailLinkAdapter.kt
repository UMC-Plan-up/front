package com.example.planup.main.my.adapter

interface EmailLinkAdapter {

    fun successEmailLink(email: String)
    fun failEmailLink(message: String)
}