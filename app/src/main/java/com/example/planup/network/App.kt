package com.example.planup.network

import android.app.Application

class App:Application() {
    companion object {
        lateinit var jwt: TokenManager
    }

    override fun onCreate() {
        jwt = TokenManager(applicationContext)
        super.onCreate()
    }

}