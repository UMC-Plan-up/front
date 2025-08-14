package com.example.planup.main.my.adapter

interface ServiceAlertAdapter {
    fun successServiceSetting(condition: Boolean)
    fun failServiceSetting(message: String)
}