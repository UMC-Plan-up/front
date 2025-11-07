package com.example.planup.main.friend.data

import com.example.planup.database.TokenSaver
import com.example.planup.database.UserInfoSaver
import com.example.planup.database.checkToken
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.main.my.data.BlockedFriend
import com.example.planup.network.ApiResult
import com.example.planup.network.FriendApi
import com.example.planup.network.dto.friend.FriendInfo
import com.example.planup.network.dto.friend.FriendReportRequestDto
import com.example.planup.network.dto.friend.FriendRequestsResult
import com.example.planup.network.dto.friend.UnblockFriendRequestDto
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * FriendRepository 구현체,
 * 실제 동작을 정의합니다.
 * @param friendApi Friend관련 네트워크 통신 모듈
 * @param tokenSaver JWT 토큰 관리 모듈
 */
class FriendRepositoryImpl @Inject constructor(
    private val friendApi: FriendApi,
    private val tokenSaver: TokenSaver,
    private val userInfoSaver: UserInfoSaver
) : FriendRepository {

    /**
     * 친구 목록을 호출합니다.
     */
    override suspend fun getFriendList(): ApiResult<List<FriendInfo>> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        friendApi.getFriendSummary(token)
                    },
                    onResponse = { friendDto ->
                        if (friendDto.isSuccess) {
                            val resultList = friendDto.result
                            val friendList =
                                resultList.firstOrNull()?.friendInfoSummaryList.orEmpty()
                            ApiResult.Success(friendList)
                        } else {
                            ApiResult.Fail(friendDto.message)
                        }
                    }
                )
            }
        }

    override suspend fun getFriendRequestList(): ApiResult<List<FriendRequestsResult>> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        friendApi.getFriendRequests(token)
                    },
                    onResponse = { friendRequests ->
                        if (friendRequests.isSuccess) {
                            val resultList = friendRequests.result
                            ApiResult.Success(resultList)
                        } else {
                            ApiResult.Fail(friendRequests.message)
                        }
                    }
                )
            }
        }

    override suspend fun getFriendBlockList(): ApiResult<List<BlockedFriend>> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    friendApi.getBlockedFriendRequest()
                },
                onResponse = { friendRequests ->
                    if (friendRequests.isSuccess) {
                        val resultList = friendRequests.result.map { blockFriendResponse ->
                            BlockedFriend(
                                id = blockFriendResponse.friendId,
                                name = blockFriendResponse.friendNickname,
                                profile = ""
                            )
                        }
                        ApiResult.Success(resultList)
                    } else {
                        ApiResult.Fail(friendRequests.message)
                    }
                }
            )
        }

    override suspend fun unBlockFriend(
        friendName: String
    ): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            val request = UnblockFriendRequestDto(
                userId = userInfoSaver.getUserId(),
                friendNickname = friendName
            )
            safeResult(
                response = {
                    friendApi.unblockFriend(
                        request
                    )
                },
                onResponse = { friendRequests ->
                    if (friendRequests.isSuccess) {
                        val unBlockSuccess = friendRequests.result
                        ApiResult.Success(unBlockSuccess)
                    } else {
                        ApiResult.Fail(friendRequests.message)
                    }
                }
            )
        }

    override suspend fun reportFriend(
        friendId: Int,
        reason: String,
        withBlock: Boolean
    ): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            val request = FriendReportRequestDto(
                userId = userInfoSaver.getUserId(),
                friendId = friendId,
                reason = reason,
                block = withBlock
            )
            safeResult(
                response = {
                    friendApi.reportFriend(request)
                },
                onResponse = { friendRequests ->
                    if (friendRequests.isSuccess) {
                        val repostSuccess = friendRequests.result
                        ApiResult.Success(repostSuccess)
                    } else {
                        ApiResult.Fail(friendRequests.message)
                    }
                }
            )
        }
}