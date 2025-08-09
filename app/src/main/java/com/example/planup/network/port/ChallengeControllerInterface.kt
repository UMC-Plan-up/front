package com.example.planup.network.port

import com.example.planup.network.data.challenge.GetChallengeFriends
import com.example.planup.network.data.challenge.PatchChallenge
import com.example.planup.network.dto.ChallengeDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface ChallengeControllerInterface {
    @PATCH("challenges/create")
    fun createChallenge(@Body challengeDto: ChallengeDto): Call<PatchChallenge>
    @GET("challenges/friends")
    fun showFriends(@Query("userId") userId: Int): Call<GetChallengeFriends>
}