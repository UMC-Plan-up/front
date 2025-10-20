package com.example.planup.main.friend.domain

import com.example.planup.main.friend.data.FriendInfo
import com.example.planup.main.friend.data.FriendRequestsResponse
import com.example.planup.main.friend.data.FriendRequestsResult
import com.example.planup.network.ApiResult

/**
 * Repository 인터페이스
 * 사용할 기능을 정의한다.
 */
interface FriendRepository {

    /**
     * 친구 목록을 불러 옵니다.
     */
    suspend fun getFriendList() : ApiResult<List<FriendInfo>>

    /**
     * 친구 요청 수를 불러 옵니다.
     */

    suspend fun getFriendRequestList() : ApiResult<List<FriendRequestsResult>>
}