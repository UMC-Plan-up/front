package com.planup.planup.network.controller

import com.planup.planup.goal.adapter.AcceptChallengeAdapter
import com.planup.planup.goal.adapter.ChallengeFriendsAdapter
import com.planup.planup.goal.adapter.RejectChallengeAdapter
import com.planup.planup.goal.adapter.RepenaltyAdapter
import com.planup.planup.goal.adapter.RequestChallengeAdapter
import com.planup.planup.goal.util.toFriendItems
import com.planup.planup.network.RetrofitInstance
import com.planup.planup.network.data.ChallengeInfo
import com.planup.planup.network.data.ChallengeResponse
import com.planup.planup.network.data.ChallengeResponseNoResult
import com.planup.planup.network.data.ChallengeResult
import com.planup.planup.network.dto.challenge.ChallengeDto
import com.planup.planup.network.dto.challenge.RepenaltyDto
import com.planup.planup.network.getRetrofit
import com.planup.planup.network.port.ChallengePort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

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
        service.rejectChallenge(challengeId, userId).enqueue(object : Callback<ChallengeResponseNoResult>{
            override fun onResponse(
                call: Call<ChallengeResponseNoResult>,
                response: Response<ChallengeResponseNoResult>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    rejectChallengeAdapter.successReject()
                } else if (!response.isSuccessful && response.body() != null) {
                    rejectChallengeAdapter.failReject(response.body()!!.message)
                } else {
                    rejectChallengeAdapter.failReject("null")
                }
            }

            override fun onFailure(call: Call<ChallengeResponseNoResult>, t: Throwable) {
                rejectChallengeAdapter.failReject(t.toString())
            }

        })
    }

    //챌린지 이름 조회: 25.08.13 미구현
    fun showChallengeName(challengeId: Int, userId: Int) {
        val service = getRetrofit().create(ChallengePort::class.java)
    }
    //챌린지  결과 조회
    fun showChallengeResult(userId: Int, challengeId: Int){
        val service = getRetrofit().create(ChallengePort::class.java)
        service.challengeResult(userId, challengeId).enqueue(object : Callback<ChallengeResponse<ChallengeResult>>{
            override fun onResponse(
                call: Call<ChallengeResponse<ChallengeResult>>,
                response: Response<ChallengeResponse<ChallengeResult>>
            ) {
                if (response.isSuccessful && response.body() != null){

                } else if (!response.isSuccessful && response.body() != null){

                } else{

                }
            }

            override fun onFailure(call: Call<ChallengeResponse<ChallengeResult>>, t: Throwable) {

            }

        })
    }

    //챌린지 수락
    fun acceptChallenge(challengeId: Int, userId: Int) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.acceptChallenge(challengeId, userId)
            .enqueue(object : Callback<ChallengeResponseNoResult> {
                override fun onResponse(
                    call: Call<ChallengeResponseNoResult>,
                    response: Response<ChallengeResponseNoResult>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        acceptChallengeAdapter.successAccept()
                    } else if (!response.isSuccessful && response.body() != null) {
                        acceptChallengeAdapter.failAccept(response.body()!!.message)
                    } else {
                        acceptChallengeAdapter.failAccept("null")
                    }
                }

                override fun onFailure(call: Call<ChallengeResponseNoResult>, t: Throwable) {
                    acceptChallengeAdapter.failAccept(t.toString())
                }

            })
    }

    //챌린지에서 친구 조회
    fun showChallengeFriends(userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.friendApi.getFriendSummary()
            withContext(Dispatchers.Main){
                if (response.isSuccessful && response.body() != null) {
                    challengeFriendsAdapter.successFriends(
                        response.body()!!.result.friendInfoSummaryList.toFriendItems()
                    )
                } else if (!response.isSuccessful && response.body() != null) {
                    challengeFriendsAdapter.failFriends(response.body()!!.message)
                } else {
                    challengeFriendsAdapter.failFriends("null")
                }
            }

        }
    }

    //챌린지에 대한 다른 페널티 제안
    fun sendRepenalty(repenaltyDto: RepenaltyDto) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.changePenalty(repenaltyDto).enqueue(object : Callback<ChallengeResponseNoResult> {
            override fun onResponse(
                call: Call<ChallengeResponseNoResult>,
                response: Response<ChallengeResponseNoResult>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    repenaltyAdapter.successRepenalty()
                } else if (!response.isSuccessful && response.body() != null) {
                    repenaltyAdapter.failRepenalty(response.body()!!.message)
                } else {
                    repenaltyAdapter.failRepenalty("null")
                }
            }

            override fun onFailure(call: Call<ChallengeResponseNoResult>, t: Throwable) {
                repenaltyAdapter.failRepenalty(t.toString())
            }

        })
    }

    // 챌린지 생성 요청
    fun requestChallenge(challengeDto: ChallengeDto) {
        val service = getRetrofit().create(ChallengePort::class.java)
        service.createChallenge(challengeDto)
            .enqueue(object : Callback<ChallengeResponseNoResult> {
                override fun onResponse(
                    call: Call<ChallengeResponseNoResult>,
                    response: Response<ChallengeResponseNoResult>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        requestChallengeAdapter.successRequest()
                    } else if (!response.isSuccessful && response.body() != null) {
                        requestChallengeAdapter.failRequest(response.body()!!.message)
                    } else if (response.isSuccessful && response.body() == null){
                        requestChallengeAdapter.failRequest(response.toString())
                    } else {
                        requestChallengeAdapter.failRequest("null")
                    }
                }

                override fun onFailure(call: Call<ChallengeResponseNoResult>, t: Throwable) {
                    requestChallengeAdapter.failRequest(t.toString())
                }

            })
    }
}