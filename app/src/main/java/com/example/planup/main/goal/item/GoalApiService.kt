package com.example.planup.main.goal.item

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface GoalApiService {
    @PUT("/goals/{goalId}")
    fun editGoal(
        @Path("goalId") goalId: Long,
        @Body editGoalRequest: EditGoalRequest
    ): Call<EditGoalApiResponse<EditGoalResponse>>

    @GET("/goals/mygoal/list")
    suspend fun getMyGoalList(
        @Header("Authorization") token: String
    ): MyGoalListResponse

    @GET("/goals/friendgoal/list")
    suspend fun getFriendGoalList(
        @Header("Authorization") token: String,
        @Path ("friendId") friendId: Int
    ): FriendGoalListResponse

    @GET("/goals/mygoal/{goalId}")
    suspend fun getGoalDetail(
        @Header("Authorization") token: String,
        @Path("goalId") goalId: Int
    ): GoalDetailResponse

}