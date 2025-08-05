package com.example.planup.main.goal.item

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface GoalApiService {
    @PATCH("/goals/{goalId}/edit")
    fun editGoal(
        @Path("goalId") goalId: Long,
        @Body editGoalRequest: EditGoalRequest
    ): Call<EditGoalApiResponse<EditGoalResponse>>


}