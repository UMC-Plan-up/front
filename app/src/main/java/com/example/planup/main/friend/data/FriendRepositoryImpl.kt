package com.example.planup.main.friend.data

import com.example.planup.database.TokenSaver
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.FriendApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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


    private inline fun <T> checkToken(
        onToken: (String) -> ApiResult<T>
    ): ApiResult<T> {
        val savedToken = tokenSaver.safeToken()
        if (savedToken.isNullOrBlank()) {
            //TODO Error When Token is null or blank
            return ApiResult.Error("invalid Token")
        }

        return onToken(savedToken)
    }


    /**
     * 친구 목록을 호출합니다.
     */
    override suspend fun getFriendList(): ApiResult<List<FriendInfo>> =
        withContext(Dispatchers.IO) {
            checkToken(
                onToken = { token ->
                    try {
                        val response = friendApi.getFriendSummary(token)
                        if (response.isSuccessful) {
                            val friendDto: FriendResponseDto? = response.body()
                            if (friendDto != null) {
                                if (friendDto.isSuccess) {
                                    val resultList = friendDto.result
                                    val friendList =
                                        resultList.firstOrNull()?.friendInfoSummaryList.orEmpty()
                                    ApiResult.Success(friendList)
                                } else {
                                    ApiResult.Fail(friendDto.message)
                                }
                            } else {
                                ApiResult.Error("fail response by body is null")
                            }
                        } else {
                            ApiResult.Error("fail response by response empty")
                        }
                    } catch (e: Exception) {
                        ApiResult.Exception(e)
                    }

                }
            )
        }

    override suspend fun getFriendRequestList(): ApiResult<List<FriendRequestsResult>> =
        withContext(Dispatchers.IO) {
            checkToken(
                onToken = { token ->
                    try {
                        val response = friendApi.getFriendRequests(token)
                        if (response.isSuccessful) {
                            val friendRequests: FriendRequestsResponse? = response.body()
                            if (friendRequests != null) {
                                if (friendRequests.isSuccess) {
                                    val resultList = friendRequests.result
                                    ApiResult.Success(resultList)
                                } else {
                                    ApiResult.Fail(friendRequests.message)
                                }
                            } else {
                                ApiResult.Error("fail response by body is null")
                            }
                        } else {
                            ApiResult.Error("fail response by response empty")
                        }
                    } catch (e: Exception) {
                        ApiResult.Exception(e)
                    }
                }
            )
        }

}