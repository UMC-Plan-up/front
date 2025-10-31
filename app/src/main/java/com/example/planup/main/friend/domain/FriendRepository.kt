package com.example.planup.main.friend.domain

import com.example.planup.network.ApiResult
import com.example.planup.network.data.BlockedFriends
import com.example.planup.network.dto.friend.FriendInfo
import com.example.planup.network.dto.friend.FriendRequestsResult

/**
 * Repository 인터페이스
 * 사용할 기능을 정의한다.
 */
interface FriendRepository {

    /**
     * 친구 목록을 불러 옵니다.
     */
    suspend fun getFriendList(): ApiResult<List<FriendInfo>>

    /**
     * 친구 요청 수를 불러 옵니다.
     */

    suspend fun getFriendRequestList(): ApiResult<List<FriendRequestsResult>>


    /**
     * 차단된 친구 목록을 가져옵니다.
     */
    suspend fun getFriendBlockList(): ApiResult<List<BlockedFriends>>

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