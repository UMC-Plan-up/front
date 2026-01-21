package com.example.planup.network

import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalCreateResponse
import com.example.planup.goal.data.GoalListResponseDto
import com.example.planup.main.friend.data.ApiResponseListFriendGoalListDto
import com.example.planup.main.goal.data.GoalEditResponse
import com.example.planup.main.goal.item.ApiResponseListMyGoalListDto
import com.example.planup.main.goal.item.CreateCommentRequest
import com.example.planup.main.goal.item.CreateCommentResponse
import com.example.planup.main.goal.item.DailyAchievementResponse
import com.example.planup.main.goal.item.DailyGoalResponse
import com.example.planup.main.goal.item.DateMemoResponse
import com.example.planup.main.goal.item.EditGoalApiResponse
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.FriendGoalAchievementResponse
import com.example.planup.main.goal.item.FriendGoalListResponse
import com.example.planup.main.goal.item.FriendPhotosResponse
import com.example.planup.main.goal.item.FriendsTimerResponse
import com.example.planup.main.goal.item.GetCommentsResponse
import com.example.planup.main.goal.item.GoalDetailResponse
import com.example.planup.main.goal.item.GoalPhotosResponse
import com.example.planup.main.goal.item.MemoRequest
import com.example.planup.main.goal.item.MyGoalListResponse
import com.example.planup.main.goal.item.PostMemoResponse
import com.example.planup.main.goal.item.TotalAchievementResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GoalApi {

    @GET("/goals/mygoal/list")
    suspend fun getMyGoalList(): Response<MyGoalListResponse>

    // 목표 삭제 API
    @DELETE("/goals/{goalId}")
    suspend fun deleteGoal(
        @Path("goalId") goalId: Int
    ): Response<GoalEditResponse>

    // 목표 공개 & 비공개 API
    @PATCH("/goals/{goalId}/public")
    suspend fun setGoalPublic(
        @Path("goalId") goalId: Int
    ): Response<GoalEditResponse>

    // 목표 활성 & 비활성화 API
    @PATCH("/goals/{goalId}/active")
    suspend fun setGoalActive(
        @Query("goalId") goalId: Int
    ): Response<GoalEditResponse>

    @POST("/goals/create")
    suspend fun createGoal(
        @Body goalCreateRequest: GoalCreateRequest
    ): Response<GoalCreateResponse>

    @GET("/goals/create/list/friend")
    suspend fun getFriendGoalsByCategory(
        @Query("goalCategory") goalCategory: String
    ): Response<GoalListResponseDto>

    @GET("/goals/create/list/community")
    suspend fun getCommunityGoalsByCategory(
        @Query("goalCategory") goalCategory: String
    ): Response<GoalListResponseDto>

    @GET("/goals/daily/{date}")
    suspend fun getDailyGoal(
        @Path("date") date: String
    ): Response<DailyGoalResponse>

    @GET("/community/daily-achievement")
    suspend fun getDailyAchievement(
        @Query("targetDate") targetDate: String
    ): Response<DailyAchievementResponse>

    @GET("/community/{goalId}/total-achievement")
    suspend fun getTotalAchievement(
        @Path("goalId") goalId: Int
    ): Response<TotalAchievementResponse>

    // 친구 목표 조회 리스트 API
    @GET("/goals/friendgoal/list")
    suspend fun getFriendGoalList(
        @Query("friendId") friendId: Int
    ): Response<FriendGoalListResponse>

    @GET("/goals/friend/{friendId}/goal/{goalId}/photos")
    suspend fun getFriendPhotos(
        @Path("friendId") friendId: Int,
        @Path("goalId") goalId: Int,
        @Query("userId") userId: Int
    ): FriendPhotosResponse

    @POST("/goals/{goalId}/comment")
    suspend fun createComment(
        @Path("goalId") goalId: Int,
        @Body comment: CreateCommentRequest
    ): CreateCommentResponse

    @PUT("/goals/{goalId}")
    suspend fun editGoal(
        @Path("goalId") goalId: Int,
        @Body editGoalRequest: EditGoalRequest
    ): EditGoalApiResponse

    @GET("/goals/{goalId}/edit")
    suspend fun getEditGoal(
        @Path("goalId") goalId: Int
    ): Response<EditGoalApiResponse>

    // 내 목표 상세 조회 API
    @GET("/goals/mygoal/{goalId}")
    suspend fun getMySpecificGoalList(
        @Path("goalId") goalId: Int
    ): ApiResponseListMyGoalListDto

    @GET("/goals/mygoal/{goalId}")
    suspend fun getGoalDetail(
        @Path("goalId") goalId: Int
    ): GoalDetailResponse

    @GET("/goals/{goalId}/friendstimer")
    suspend fun getFriendsTimer(
        @Path("goalId") goalId: Int
    ): Response<FriendsTimerResponse>

    @GET("/goals/{goalId}/memo/{date}")
    suspend fun getDateMemo(
        @Path("goalId") goalId: Int,
        @Path("date") date: String
    ): Response<DateMemoResponse>

    @POST("goals/{goalId}/memo")
    suspend fun saveMemo(
        @Path("goalId") goalId: Int,
        @Body request: MemoRequest
    ): Response<PostMemoResponse>

    @GET("/goals/{goalId}/comments")
    suspend fun getComments(
        @Path("goalId") goalId: Int
    ): GetCommentsResponse

    @GET("/community/friend/{friendId}/goal/{goalId}/total-achievement")
    suspend fun getFriendGoalAchievement(
        @Path("friendId") friendId: Int,
        @Path("goalId") goalId: Int
    ): Response<FriendGoalAchievementResponse>

    @GET("/goals/{goalId}/photos")
    suspend fun getGoalPhotos(
        @Path("goalId") goalId: Int
    ): GoalPhotosResponse
}