package com.planup.planup.main.my.adapter

interface KakaoAdapter {
    fun successKakao(kakaoAddr: String?)
    fun failKakao(message: String)
}