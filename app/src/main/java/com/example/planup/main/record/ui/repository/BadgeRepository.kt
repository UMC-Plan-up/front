package com.example.planup.main.record.ui.repository

import com.example.planup.main.record.data.BadgeListResult
import com.example.planup.network.ApiResult
import com.example.planup.network.RecordApi
import com.example.planup.network.safeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BadgeRepository @Inject constructor(
    private val recordApi: RecordApi
){
    suspend fun loadBadgeList(): ApiResult<List<BadgeListResult>> =
        withContext(Dispatchers.IO) {
            safeResult(
                response = {
                    recordApi.getBadgeList()
                },
                onResponse = { badgeListResponse ->
                    if (badgeListResponse.isSuccess) {
                        val resultList = badgeListResponse.result
                        ApiResult.Success(resultList)
                    } else {
                        ApiResult.Fail(badgeListResponse.message)
                    }
                }
            )
        }
}