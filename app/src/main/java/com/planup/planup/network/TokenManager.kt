package com.planup.planup.network

import com.planup.planup.database.TokenSaver

/*JWT 토큰 관리를 위한 클래스
* sharedPreferences를 통해 토큰을 저장하고 업데이트 함*/
class TokenManager(
    private val tokenSaver: TokenSaver
) {
    // 토큰 관련 기능 정리될 때 까지 임시로 추가
    var token: String?
        get() = tokenSaver.getToken()?.let {
            if(it.startsWith("Bearer")) it else "Bearer $it"
        }
        set(value) {
            tokenSaver.saveToken(value)
        }
}