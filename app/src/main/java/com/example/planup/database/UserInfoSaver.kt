package com.example.planup.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

/**
 * 사용자 정보 관련 저장
 */
class UserInfoSaver(
    context: Context
) {
    companion object {
        private const val PREF_TOKEN_NAME = "userInfo"

        private const val KEY_NICKNAME = "nickname"
    }

    private val prefs = context.getSharedPreferences(PREF_TOKEN_NAME, MODE_PRIVATE)

    /**
     * 현재 저장된 닉네임을 가져옵니다.
     */
    fun getNickName(): String {
        return prefs.getString(KEY_NICKNAME, "") ?: ""
    }

    /**
     * 닉네임을 저장합니다.
     */
    fun saveNickName(nickName: String) {
        prefs.edit {
            putString(KEY_NICKNAME, nickName)
        }
    }

    /**
     * 저장된 모든 유저 정보를 지웁니다.
     * (용도) - 로그아우
     */
    fun clearAllUserInfo(){
        prefs.edit {
            clear()
        }
    }
}