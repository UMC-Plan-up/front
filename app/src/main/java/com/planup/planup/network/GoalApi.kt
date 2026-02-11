package com.example.planup.network

import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.data.GoalCreateResponse
import com.example.planup.goal.data.GoalListResponseDto
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
import com.example.planup.main.goal.item.GoalCreateLevelResponse
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
        @Query("goalId") goalId: Int
    ): Response<GoalEditResponse>

    // 목표 공개 & 비공개 API
    @PATCH("/goals/{goalId}/public")
    suspend fun setGoalPublic(
        @Query("goalId") goalId: Int
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

    // http://54.180.207.84/community/daily-achievement?targetDate=2024-08-21
    @GET("/goals/daily/{date}")
    suspend fun getDailyGoal(
        @Query("date") date: String
    ): Response<DailyGoalResponse>
    //http://54.180.207.84/community/daily-achievement?targetDate=2024-08-21
    @GET("/community/daily-achievement")
    suspend fun getDailyAchievement(
        @Query("targetDate") targetDate: String
    ): Response<DailyAchievementResponse>

    //http://54.180.207.84/community/1/total-achievement
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
        @Query("userId") userId: Int // ? 스웨거에 안보임
    ): FriendPhotosResponse

    // http://54.180.207.84/goals/1/comments
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

    //http://54.180.207.84/goals/{goalId}?goalId=1
    @GET("/goals/{goalId}/edit")
    suspend fun getEditGoal(
        @Query("goalId") goalId: Int
    ): Response<EditGoalApiResponse>

    // 내 목표 상세 조회 API
    @GET("/goals/mygoal/{goalId}")
    suspend fun getMySpecificGoalList(
        @Path("goalId") goalId: Int
    ): ApiResponseListMyGoalListDto

    // http://54.180.207.84/goals/mygoal/{goalId}?goalId=1
    @GET("/goals/mygoal/{goalId}")
    suspend fun getGoalDetail(
        @Query("goalId") goalId: Int
    ): GoalDetailResponse

    // http://54.180.207.84/goals/1/friendstimer
    @GET("/goals/{goalId}/friendstimer")
    suspend fun getFriendsTimer(
        @Path("goalId") goalId: Int
    ): Response<FriendsTimerResponse>

    // http://54.180.207.84/goals/1/memo/1
    @GET("/goals/{goalId}/memo/{date}")
    suspend fun getDateMemo(
        @Path("goalId") goalId: Int,
        @Path("date") date: String
    ): Response<DateMemoResponse>
    // http://54.180.207.84/goals/1/memo
    @POST("goals/{goalId}/memo")
    suspend fun saveMemo(
        @Path("goalId") goalId: Int,
        @Body request: MemoRequest
    ): Response<PostMemoResponse>
    // http://54.180.207.84/goals/1/comments
    @GET("/goals/{goalId}/comments")
    suspend fun getComments(
        @Path("goalId") goalId: Int
    ): GetCommentsResponse
    //http://54.180.207.84/community/friend/2/goal/1/total-achievement
    @GET("/community/friend/{friendId}/goal/{goalId}/total-achievement")
    suspend fun getFriendGoalAchievement(
        @Path("friendId") friendId: Int,
        @Path("goalId") goalId: Int
    ): Response<FriendGoalAchievementResponse>
    //http://54.180.207.84/goals/{goalId}/photos?goalId=1
    @GET("/goals/{goalId}/photos")
    suspend fun getGoalPhotos(
        @Path("goalId") goalId: Int
    ): Response<GoalPhotosResponse>

    @POST("/community/{goalId}/join")
    suspend fun joinGoal(
        @Path("goalId") goalId: Int
    ): Response<GoalJoinResponseDto>

    @GET("/goals/level")
    suspend fun getGoalLevel(): Response<GoalCreateLevelResponse>
}