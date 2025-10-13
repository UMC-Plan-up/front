package com.example.planup.main.friend.data

import com.example.planup.database.TokenSaver
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.network.FriendApi
import javax.inject.Inject
import kotlin.collections.orEmpty


/**
 * FriendRepository 구현체,
 * 실제 동작을 정의합니다.
 * @param friendApi Friend관련 네트워크 통신 모듈
 * @param tokenSaver JWT 토큰 관리 모듈
 */
class FriendRepositoryImpl @Inject constructor(
    private val friendApi: FriendApi,
    private val tokenSaver: TokenSaver
) : FriendRepository {


    /**
     * 친구 목록을 호출합니다.
     * TODO...
     */
    override suspend fun getFriendList(): List<FriendInfo>? {
        val savedToken = tokenSaver.getToken()
        if (savedToken.isNullOrBlank()) {
            //TODO Error When Token is null or blank
            return null
        }
        val response = friendApi.getFriendSummary(savedToken)
        if (response.isSuccessful && response.body()?.isSuccess == true) {
            val resultList = response.body()!!.result
            val friendList = resultList.firstOrNull()?.friendInfoSummaryList.orEmpty()

            return friendList
        } else {
            return null
        }
    }

}