package com.example.planup.network.controller

import android.util.Log
import com.example.planup.goal.adapter.AcceptChallengeAdapter
import com.example.planup.goal.adapter.ChallengeFriendsAdapter
import com.example.planup.goal.adapter.RejectChallengeAdapter
import com.example.planup.goal.adapter.RequestChallengeAdapter
import com.example.planup.goal.adapter.RepenaltyAdapter
import com.example.planup.network.data.ChallengeFriends
import com.example.planup.network.data.ChallengeInfo
import com.example.planup.network.data.ChallengeResponse
import com.example.planup.network.dto.challenge.ChallengeDto
import com.example.planup.network.dto.challenge.RepenaltyDto
import com.example.planup.network.getRetrofit
import com.example.planup.network.port.ChallengePort
import okhttp3.Challenge
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeController {

    /*
* Adapter는 각 API 서비스 응답에 대한 레이아웃 변화를 관리함
* 레이아웃 관리하는 .kt 파일에서 해당 인터페이스를 구현하여 API 응답 반영함*/

    //챌린지 정보 조회: 25.08.13 미구현
    //챌린지 거절
    private lateinit var rejectChallengeAdapter: RejectChallengeAdapter
    fun setRejectChallengeAdapter(adapter: RejectChallengeAdapter){
        this.rejectChallengeAdapter = adapter
    }
    //챌린지 이름 조회: 25.08.13 미구현
    //챌린지 수락
    private lateinit var acceptChallengeAdapter: AcceptChallengeAdapter
    fun setAcceptChallengeAdapter(adapter: AcceptChallengeAdapter){
        this.acceptChallengeAdapter = adapter
    }
    //챌린지에서 친구 조회
    private lateinit var challengeFriendsAdapter: ChallengeFriendsAdapter
    fun setChallengeFriendsAdapter(adapter: ChallengeFriendsAdapter) {
        this.challengeFriendsAdapter = adapter
    }

    //챌린지에 대한 다른 페널티 제안
    private lateinit var repenaltyAdapter: RepenaltyAdapter
    fun setRepenaltyAdapter(adapter: RepenaltyAdapter) {
        this.repenaltyAdapter = adapter
    }

    //챌린지 생성 요청
    private lateinit var requestChallengeAdapter: RequestChallengeAdapter
    fun setRequestChallengeAdapter(adapter: RequestChallengeAdapter) {
        this.requestChallengeAdapter = adapter
    }

    //챌린지 정보 조회: 25.08.13 미구현
    fun challengeInfo(challengeId: Int) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.challengeInfo(challengeId)
            .enqueue(object : Callback<ChallengeResponse<ChallengeInfo>> {
                override fun onResponse(
                    call: Call<ChallengeResponse<ChallengeInfo>>,
                    response: Response<ChallengeResponse<ChallengeInfo>>
                ) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(call: Call<ChallengeResponse<ChallengeInfo>>, t: Throwable) {
                    TODO("Not yet implemented")
                }


            })
    }

    //챌린지 거절
    fun rejectChallenge(challengeId: Int, userId: Int) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.rejectChallenge(challengeId, userId).enqueue(object : Callback<ChallengeResponse<Boolean>>{
            override fun onResponse(
                call: Call<ChallengeResponse<Boolean>>,
                response: Response<ChallengeResponse<Boolean>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    rejectChallengeAdapter.successReject()
                } else {
                    rejectChallengeAdapter.failReject(response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<ChallengeResponse<Boolean>>, t: Throwable) {
                rejectChallengeAdapter.failReject(t.toString())
            }

        })
    }

    //챌린지 이름 조회: 25.08.13 미구현
    fun showChallengeName(challengeId: Int, userId: Int) {
        val service = getRetrofit().create(ChallengePort::class.java)
    }

    //챌린지 수락
    fun acceptChallenge(challengeId: Int, userId: Int) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.acceptChallenge(challengeId, userId)
            .enqueue(object : Callback<ChallengeResponse<Boolean>> {
                override fun onResponse(
                    call: Call<ChallengeResponse<Boolean>>,
                    response: Response<ChallengeResponse<Boolean>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        acceptChallengeAdapter.successAccept()
                    } else {
                        acceptChallengeAdapter.failAccept(response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<ChallengeResponse<Boolean>>, t: Throwable) {
                    acceptChallengeAdapter.failAccept(t.toString())
                }

            })
    }

    //챌린지에서 친구 조회
    fun showChallengeFriends(userId: Int) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.showFriends(userId)
            .enqueue(object : Callback<ChallengeResponse<List<ChallengeFriends>>> {
                override fun onResponse(
                    call: Call<ChallengeResponse<List<ChallengeFriends>>>,
                    response: Response<ChallengeResponse<List<ChallengeFriends>>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        challengeFriendsAdapter.successFriends(response.body()!!.result)
                    } else {
                        challengeFriendsAdapter.failFriends(response.body()!!.message)
                    }
                }

                override fun onFailure(
                    call: Call<ChallengeResponse<List<ChallengeFriends>>>,
                    t: Throwable
                ) {
                    challengeFriendsAdapter.failFriends(t.toString())
                }

            })
    }

    //챌린지에 대한 다른 페널티 제안
    fun sendRepenalty(repenaltyDto: RepenaltyDto) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.changePenalty(repenaltyDto).enqueue(object : Callback<ChallengeResponse<Boolean>> {
            override fun onResponse(
                call: Call<ChallengeResponse<Boolean>>,
                response: Response<ChallengeResponse<Boolean>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    repenaltyAdapter.successRepenalty()
                } else {
                    repenaltyAdapter.failRepenalty(response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<ChallengeResponse<Boolean>>, t: Throwable) {
                repenaltyAdapter.failRepenalty(t.toString())
            }

        })
    }

    // 챌린지 생성 요청
    fun requestChallenge(challengeDto: ChallengeDto) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.createChallenge(challengeDto)
            .enqueue(object : Callback<ChallengeResponse<Boolean>> {
                override fun onResponse(
                    call: Call<ChallengeResponse<Boolean>>,
                    response: Response<ChallengeResponse<Boolean>>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        requestChallengeAdapter.successRequest()
                    } else {
                        requestChallengeAdapter.failRequest(response.body()!!.message)

                    }
                }

                override fun onFailure(call: Call<ChallengeResponse<Boolean>>, t: Throwable) {
                    requestChallengeAdapter.failRequest(t.toString())
                }

            })
    }
}