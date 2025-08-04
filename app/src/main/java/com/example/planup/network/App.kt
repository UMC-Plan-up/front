package com.example.planup.network

import android.app.Application

class App:Application() {
    companion object {
        lateinit var prefs: TokenManager
    }

    override fun onCreate() {
        prefs = TokenManager(applicationContext)
        super.onCreate()
    }

}