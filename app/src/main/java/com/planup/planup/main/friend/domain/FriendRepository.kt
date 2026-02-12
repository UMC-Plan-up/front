package com.planup.planup.main.friend.domain

import com.planup.planup.main.my.data.BlockedFriend
import com.planup.planup.network.ApiResult
import com.planup.planup.network.dto.friend.FriendInfo
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

    fun getFriendRequestList(): Flow<List<FriendInfo>>


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

    suspend fun fetchFriendRequestList(): ApiResult<List<FriendInfo>>


    /**
     * 차단된 친구 목록을 갱신 합니다..
     */
    suspend fun fetchFriendBlockList(): ApiResult<List<BlockedFriend>>


    /**
     * 친구 관계를 삭제 합니다.
     */
    suspend fun deleteFriend(
        friendId: Int
    ) : ApiResult<Boolean>

    /**
     * 친구를 차단 합니다.
     */
    suspend fun blockFriend(
        friendId: Int
    ): ApiResult<Boolean>

    /**
     * 차단된 친구를 해제한다.
     */
    suspend fun unBlockFriend(
        friendId: Int
    ): ApiResult<Boolean>

    /**
     * 친구를 신고한다.
     */
    suspend fun reportFriend(
        friendId: Int,
        reason: String,
        withBlock: Boolean
    ): ApiResult<Boolean>

    /**
     * 친구 요청을 수락한다.
     *
     * @param friendId
     */
    suspend fun acceptFriend(
        friendId: Int
    ) : ApiResult<Boolean>

    /**
     * 친구 요청을 거절 한다.
     *
     * @param friendId
     * @return
     */
    suspend fun declineFriend(
        friendId: Int
    ): ApiResult<Boolean>

}