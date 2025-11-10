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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private var _friendList = MutableStateFlow(listOf<FriendInfo>())
    private val friendList = _friendList.asStateFlow()

    private var _friendRequestList = MutableStateFlow(listOf<FriendRequestsResult>())
    private val friendRequestList = _friendRequestList.asStateFlow()

    private var _blockFriendList = MutableStateFlow(emptyList<BlockedFriend>())
    private val blockFriendList = _blockFriendList.asStateFlow()

    override fun getFriendList(): Flow<List<FriendInfo>> = friendList

    override fun getFriendRequestList(): Flow<List<FriendRequestsResult>> =
        friendRequestList

    override fun getFriendBlockList(): Flow<List<BlockedFriend>> = blockFriendList

    /**
     * 친구 목록을 호출합니다.
     */
    override suspend fun fetchFriendList(): ApiResult<List<FriendInfo>> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        friendApi.getFriendSummary(token)
                    },
                    onResponse = { friendDto ->
                        if (friendDto.isSuccess) {
                            val resultList = friendDto.result
                            val friendList = resultList.firstOrNull()?.friendInfoSummaryList.orEmpty()
                            _friendList.update {
                                friendList
                            }
                            ApiResult.Success(friendList)
                        } else {
                            ApiResult.Fail(friendDto.message)
                        }
                    }
                )
            }
        }

    override suspend fun fetchFriendRequestList(): ApiResult<List<FriendRequestsResult>> =
        withContext(Dispatchers.IO) {
            tokenSaver.checkToken { token ->
                safeResult(
                    response = {
                        friendApi.getFriendRequests(token)
                    },
                    onResponse = { friendRequests ->
                        if (friendRequests.isSuccess) {
                            val resultList = friendRequests.result
                            _friendRequestList.update {
                                resultList
                            }
                            ApiResult.Success(resultList)
                        } else {
                            ApiResult.Fail(friendRequests.message)
                        }
                    }
                )
            }
        }

    override suspend fun fetchFriendBlockList(): ApiResult<List<BlockedFriend>> =
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
                        _blockFriendList.update {
                            resultList
                        }
                        ApiResult.Success(resultList)
                    } else {
                        ApiResult.Fail(friendRequests.message)
                    }
                }
            )
        }

    override suspend fun deleteFriend(
        friendId: Int
    ): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    friendApi.deleteFriend(
                        friendId
                    )
                },
                onResponse = { friendDeleteResponse ->
                    if (friendDeleteResponse.isSuccess) {
                        val deleteSuccess = friendDeleteResponse.result
                        //삭제 성공 했다면 다시 요청 하지 않고 직접 친구 목록에서 해당 id를 삭제 요청한다.
                        _friendList.update {
                            it.filter { friendInfo ->
                                friendInfo.id != friendId
                            }
                        }
                        ApiResult.Success(deleteSuccess)
                    } else {
                        ApiResult.Fail(friendDeleteResponse.message)
                    }
                }
            )
        }
    override suspend fun blockFriend(
        friendId: Int
    ): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    friendApi.blockFriend(
                        friendId
                    )
                },
                onResponse = { friendBlockResponse ->
                    if (friendBlockResponse.isSuccess) {
                        val blockSuccess = friendBlockResponse.result
                        fetchFriendList()
                        fetchFriendBlockList()
                        ApiResult.Success(blockSuccess)
                    } else {
                        ApiResult.Fail(friendBlockResponse.message)
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
                        fetchFriendList()
                        fetchFriendBlockList()
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
                        fetchFriendList()
                        fetchFriendBlockList()
                        ApiResult.Success(repostSuccess)
                    } else {
                        ApiResult.Fail(friendRequests.message)
                    }
                }
            )
        }

    override suspend fun acceptFriend(
        friendId: Int
    ): ApiResult<Boolean> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    friendApi.acceptFriend(friendId)
                },
                onResponse = { friendAcceptResponse ->
                    if (friendAcceptResponse.isSuccess) {
                        val acceptResult = friendAcceptResponse.result
                        fetchFriendList()
                        _friendRequestList.update {
                            it.filter { friendRequest ->
                                friendRequest.id != friendId
                            }
                        }
                        ApiResult.Success(acceptResult)
                    } else {
                        ApiResult.Fail(friendAcceptResponse.message)
                    }
                }
            )
        }

    override suspend fun declineFriend(
        friendId: Int
    ): ApiResult<Boolean> =  withContext(Dispatchers.IO) {
        safeResult(
            response = {
                friendApi.rejectFriend(friendId)
            },
            onResponse = { friendRejectResponse ->
                if (friendRejectResponse.isSuccess) {
                    val blockSuccess = friendRejectResponse.result
                    fetchFriendList()
                    _friendRequestList.update {
                        it.filter { friendRequest ->
                            friendRequest.id != friendId
                        }
                    }
                    ApiResult.Success(blockSuccess)
                } else {
                    ApiResult.Fail(friendRejectResponse.message)
                }
            }
        )
    }
}