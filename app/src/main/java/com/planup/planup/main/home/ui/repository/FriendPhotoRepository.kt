package com.planup.planup.main.home.ui.repository

import com.planup.planup.network.ApiResult
import com.planup.planup.network.FriendApi
import com.planup.planup.network.GoalApi
import com.planup.planup.network.dto.friend.FriendReportRequestDto
import com.planup.planup.network.safeResult
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class FriendPhotoRepository @Inject constructor(
    private val friendApi: FriendApi,
    private val goalApi: GoalApi
){
    suspend fun postReport(friendId: Int, reason: String, block: Boolean) =
        withContext(Dispatchers.IO){
            safeResult(
                response = {
                    friendApi.reportFriend(
                        FriendReportRequestDto(
                            friendId, reason, block
                        )
                    )
                },
                onResponse = {
                    if(it.isSuccess) {
                        ApiResult.Success(it.result)
                    } else {
                        ApiResult.Fail(it.message)
                    }
                }
            )
        }

    suspend fun loadFriendPhotos(friendId: Int, goalId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getFriendPhotos(friendId, goalId)
                },
                onResponse = {
                    if(it.isSuccess) {
                        ApiResult.Success(it.result)
                    } else {
                        ApiResult.Fail(it.message)
                    }
                }
            )
        }
}