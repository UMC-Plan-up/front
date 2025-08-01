package com.example.planup.password.data

import com.example.planup.network.PasswordApi

class PasswordRepository(private val api: PasswordApi) {
    suspend fun verifyPassword(userId: Long, password: String): Boolean {
        val response = api.verifyPassword(userId, password)
        return if (response.isSuccessful) {
            response.body()?.result ?: false
        } else {
            false
        }
    }
}