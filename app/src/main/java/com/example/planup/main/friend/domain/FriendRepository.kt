package com.example.planup.main.friend.domain

import com.example.planup.main.my.data.BlockedFriend
import com.example.planup.network.ApiResult
import com.example.planup.network.dto.friend.FriendInfo
import com.example.planup.network.dto.friend.FriendRequestsResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository 인터페이스
 * 사용할 기능을 정의한다.
 */
interface FriendRepository {

    /**
     * 친구 목록을 불러 옵니다.
     */
    fun getFriendList(): Flow<List<FriendInfo>>

    /**
     * 친구 요청 수를 불러 옵니다.
     */

    fun getFriendRequestList(): Flow<List<FriendRequestsResult>>


    /**
     * 차단된 친구 목록을 가져옵니다.
     */
    fun getFriendBlockList(): Flow<List<BlockedFriend>>

    /**
     * 친구 목록을 갱신합니다.
     */
    suspend fun fetchFriendList(): ApiResult<List<FriendInfo>>

    /**
     * 친구 요청 수를 갱신 합니다.
     */

    suspend fun fetchFriendRequestList(): ApiResult<List<FriendRequestsResult>>


    /**
     * 차단된 친구 목록을 갱신 합니다..
     */
    suspend fun fetchFriendBlockList(): ApiResult<List<BlockedFriend>>


    suspend fun blockFriend(
        friendId: Int
    ): ApiResult<Boolean>

    /**
     * 차단된 친구를 해제한다.
     */
    suspend fun unBlockFriend(
        friendName: String
    ): ApiResult<Boolean>

    /**
     * 친구를 신고한다.
     */
    suspend fun reportFriend(
        friendId: Int,
        reason: String,
        withBlock: Boolean
    ): ApiResult<Boolean>

}