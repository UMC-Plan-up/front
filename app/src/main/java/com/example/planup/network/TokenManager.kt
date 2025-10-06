package com.example.planup.network

import com.example.planup.database.TokenSaver

/*JWT 토큰 관리를 위한 클래스
* sharedPreferences를 통해 토큰을 저장하고 업데이트 함*/
class TokenManager(
    private val tokenSaver: TokenSaver
) {
    var token: String?
        get() = tokenSaver.getToken()
        set(value) {
            tokenSaver.saveToken(value)
        }
}