package com.example.planup.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class TokenSaver(
    context: Context
) {
    companion object {
        private const val PREF_TOKEN_NAME = "Token"
        private const val TOKEN_KEY = "token"
    }

    private val prefs = context.getSharedPreferences(PREF_TOKEN_NAME, MODE_PRIVATE)

    fun saveToken(token: String?) {
        prefs.edit {
            putString(TOKEN_KEY,token)
        }
    }

    fun getToken() : String? {
        return prefs.getString(TOKEN_KEY,null)
    }
}