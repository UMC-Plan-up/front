package com.example.planup.main.goal.item

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GoalApiService {

    @PUT("/goals/{goalId}")
    suspend fun editGoal(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int,
        @Body editGoalRequest: EditGoalRequest
    ): EditGoalApiResponse

    @GET("/goals/{goalId}/edit")
    suspend fun getEditGoal(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int
    ): EditGoalApiResponse

    @GET("/goals/mygoal/list")
    suspend fun getMyGoalList(
        @Header("Authorization") token: String
    ): MyGoalListResponse

    // 내 목표 상세 조회 API
    @GET("/goals/mygoal/{goalId}")
    suspend fun getMySpecificGoalList(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int
    ): ApiResponseListMyGoalListDto
    @GET("/goals/friendgoal/list")
    suspend fun getFriendGoalList(
        @Header("Authorization") token: String,
        @Query ("friendId") friendId: Int
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

    @GET("/goals/{goalId}/memo/{date}")
    suspend fun getDateMemo(
        @Header("Authorization") token: String,
        @Path("goalId") goalId: Int,
        @Path("date") date: String
    ): DateMemoResponse

    @POST("goals/{goalId}/memo")
    suspend fun saveMemo(
        @Header("Authorization") token: String,
        @Path("goalId") goalId: Int,
        @Body request: MemoRequest
    ): PostMemoResponse

    @GET("/goals/{goalId}/comments")
    suspend fun getComments(
        @Header("Authorization") token: String,
        @Path("goalId") goalId: Int
    ): GetCommentsResponse

    @GET("/community/daily-achievement")
    suspend fun getDailyAchievement(
        @Header("Authorization") token: String,
        @Query("targetDate") targetDate: String
    ): DailyAchievementResponse

    @GET("/community/friend/{friendId}/goal/{goalId}/total-achievement")
    suspend fun getFriendGoalAchievement(
        @Header("Authorization") token: String,
        @Path("friendId") friendId: Int,
        @Path("goalId") goalId: Int
    ): FriendGoalAchievementResponse

    @GET("/goals/{goalId}/photos")
    suspend fun getGoalPhotos(
        @Header("Authorization") token: String,
        @Query("goalId") goalId: Int
    ): GoalPhotosResponse
}