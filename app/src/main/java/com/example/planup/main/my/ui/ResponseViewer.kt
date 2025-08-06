package com.example.planup.main.my.ui

interface ResponseViewer {
    fun onResponseSuccess()
    fun onResponseError(code: String, message: String)
}