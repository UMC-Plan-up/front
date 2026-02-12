package com.planup.planup.network.adapter

interface KakaoLinkAdapter {
    fun successKakaoLink(email: String)
    fun failKakaoLink(message: String)
}