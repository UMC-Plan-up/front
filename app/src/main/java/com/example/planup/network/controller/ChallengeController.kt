package com.example.planup.network.controller

import android.util.Log
import com.example.planup.goal.adapter.ChallengeFriendsAdapter
import com.example.planup.goal.adapter.RequestChallengeAdapter
import com.example.planup.network.data.challenge.GetChallengeFriends
import com.example.planup.network.data.challenge.PatchChallenge
import com.example.planup.network.dto.ChallengeDto
import com.example.planup.network.getRetrofit
import com.example.planup.network.port.ChallengeControllerInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeController{
    private lateinit var requestChallengeAdapter: RequestChallengeAdapter
    fun setRequestChallengeAdapter(adapter: RequestChallengeAdapter){
        this.requestChallengeAdapter = adapter
    }
    private lateinit var challengeFriendsAdapter: ChallengeFriendsAdapter
    fun setChallengeFriendsAdapter(adapter: ChallengeFriendsAdapter){
        this.challengeFriendsAdapter = adapter
    }
    fun requestChallenge(challengeDto: ChallengeDto){
        val service = getRetrofit().create(ChallengeControllerInterface::class.java)
        service.createChallenge(challengeDto).enqueue(object : retrofit2.Callback<PatchChallenge>{
            override fun onResponse(
                call: Call<PatchChallenge>,
                response: Response<PatchChallenge>
            ) {
                when(response.isSuccessful){
                    true -> {
                        requestChallengeAdapter.successRequest()
                    }
                    else ->{
                        requestChallengeAdapter.failRequest(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<PatchChallenge>, t: Throwable) {
                Log.d("okhttp","fail: $t")
            }

        })
    }
    fun showChallengeFriends(userId: Int){
        val service = getRetrofit().create(ChallengeControllerInterface::class.java)
        service.showFriends(userId).enqueue(object: Callback<GetChallengeFriends>{
            override fun onResponse(call: Call<GetChallengeFriends>, response: Response<GetChallengeFriends>) {
                when(response.isSuccessful){
                    true -> {
                        challengeFriendsAdapter.successFriends(response.body()!!.result)
                    }
                    else -> {
                        Log.d("okhttp","showChallengeFriends: ${response.body()}")
                        challengeFriendsAdapter.failFriends(response.body()!!.message)
                    }
                }
            }

            override fun onFailure(call: Call<GetChallengeFriends>, t: Throwable) {
                Log.d("okhttp","fail: $t")
            }

        })
    }

}