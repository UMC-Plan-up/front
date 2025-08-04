package com.example.planup.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

/*JWT 토큰 관리를 위한 클래스
* sharedPreferences를 통해 토큰을 저장하고 업데이트 함*/
class TokenManager(context: Context) {
    private val prefNm="Token"
    private val prefs=context.getSharedPreferences(prefNm,MODE_PRIVATE)
    var token:String?
        get() = prefs.getString("token",null)
        set(value){
            prefs.edit { putString("token", value) }
        }
}