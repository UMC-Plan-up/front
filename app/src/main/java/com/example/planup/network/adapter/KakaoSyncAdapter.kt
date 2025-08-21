package com.example.planup.network.adapter

interface KakaoSyncAdapter {
    fun successKakaoSync(email: String)
    fun failKakaoSync(message: String)
}