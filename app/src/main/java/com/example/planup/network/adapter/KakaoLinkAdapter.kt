package com.example.planup.network.adapter

interface KakaoLinkAdapter {
    fun successKakaoLink(email: String)
    fun failKakaoLink(message: String)
}