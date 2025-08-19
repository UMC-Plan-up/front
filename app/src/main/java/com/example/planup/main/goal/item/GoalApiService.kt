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
    suspend fun editGoal(
        @Header("Authorization") token: String,
        @Path("goalId") goalId: Int,
        @Body editGoalRequest: EditGoalRequest
    ): EditGoalApiResponse

    @GET("/goals/{goalId}/edit")
    suspend fun getEditGoal(
        @Header("Authorization") token: String,
        @Path("goalId") goalId: Int
    ): EditGoalApiResponse

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

    @GET("/goals/{goalId}/friendstimer")
    suspend fun getFriendsTimer(
        @Header("Authorization") token: String,
        @Path("goalId") goalId: Int
    ): FriendsTimerResponse

    @GET("/goals/daily/{date}")
    suspend fun getDailyGoal(
        @Header("Authorization") token: String,
        @Path("date") date: String
    ): DailyGoalResponse

}