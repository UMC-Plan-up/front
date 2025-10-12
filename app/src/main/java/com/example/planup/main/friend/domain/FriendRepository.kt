package com.example.planup.main.friend.domain

import com.example.planup.main.friend.data.FriendInfo

/**
 * Repository 인터페이스
 * 사용할 기능을 정의한다.
 */
interface FriendRepository {

    /**
     * 친구 목록을 불러 옵니다.
     */
    suspend fun getFriendList() : List<FriendInfo>?
}