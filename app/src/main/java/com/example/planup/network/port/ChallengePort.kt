package com.example.planup.network.port

import com.example.planup.network.data.ChallengeResponse
import com.example.planup.network.data.ChallengeFriends
import com.example.planup.network.data.ChallengeInfo
import com.example.planup.network.data.ChallengeResponseNoResult
import com.example.planup.network.data.ChallengeResultResponse
import com.example.planup.network.dto.challenge.ChallengeDto
import com.example.planup.network.dto.challenge.RepenaltyDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChallengePort {

    //챌린지 정보 조회
    @GET("challenges/{challengeId}")
    fun challengeInfo(@Path("challengeId") challengeId: Int): Call<ChallengeResponse<ChallengeInfo>>
    //챌린지 결과 조회
    @GET("challenges/{challengeId/result")
    fun challengeResult(@Path("challengeId") challengeId: Int, @Query("userId") userId: Int): Call<ChallengeResultResponse>
    //챌린지 거절
    @GET("challenges/{challengeId}/reject")
    fun rejectChallenge(@Path("challengeId") challengeId: Int, @Query("userId") userId: Int): Call<ChallengeResponseNoResult>
    //챌린지 이름 조회
    @GET("challenges/{challengeId}/name")
    fun challengeName(@Path("challengeId") challengeId: Int, @Query("userId") userId: Int): Call<ChallengeResponse<String>>
    //챌린지 수학
    @GET("challenges/{challengeId}/accept")
    fun acceptChallenge(@Path("challengeId") challengeId: Int, @Query("userId") userId: Int): Call<ChallengeResponseNoResult>
    //챌린지에서 친구 조회
    @GET("challenges/friends")
    fun showFriends(@Query("userId") userId: Int): Call<ChallengeResponse<List<ChallengeFriends>>>

    //챌린지에 대한 다른 페널티 제안
    @POST("challenges/repanalty")
    fun changePenalty(@Body repenaltyDto: RepenaltyDto): Call<ChallengeResponseNoResult>
    //챌린지 생성 요청
    @POST("challenges/create")
    fun createChallenge(@Body challengeDto: ChallengeDto): Call<ChallengeResponseNoResult>
}