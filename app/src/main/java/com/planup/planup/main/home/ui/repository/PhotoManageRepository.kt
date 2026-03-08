package com.planup.planup.main.home.ui.repository

import com.planup.planup.main.goal.item.PostPhotoRequest
import com.planup.planup.network.GoalApi
import com.planup.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.planup.planup.network.ApiResult

@Singleton
class PhotoManageRepository @Inject constructor(
    private val goalApi: GoalApi
){
    suspend fun loadPhotos(goalId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.getGoalPhotos(goalId)
                },
                onResponse = {
                    if (it.isSuccess) {
                        ApiResult.Success(it.result)
                    } else {
                        ApiResult.Fail(it.message)
                    }
                }
            )
        }

    suspend fun postPhotos(goalId: Int, date: String, photoList: List<String>) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.postPhoto(goalId, date,
                        PostPhotoRequest(photoList)
                    )
                },
                onResponse = {
                    if (it.isSuccess) {
                        ApiResult.Success(it.result)
                    } else {
                        ApiResult.Fail(it.message)
                    }
                }
            )
        }

    suspend fun deletePhotos(photoId: Int) =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    goalApi.deletePhoto(photoId)
                },
                onResponse = {
                    if (it.isSuccess) {
                        ApiResult.Success(it.result)
                    } else {
                        ApiResult.Fail(it.message)
                    }
                }
            )
        }
}