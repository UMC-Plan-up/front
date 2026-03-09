package com.planup.planup.network.controller

import com.planup.planup.goal.adapter.AcceptChallengeAdapter
import com.planup.planup.goal.adapter.ChallengeFriendsAdapter
import com.planup.planup.goal.adapter.RejectChallengeAdapter
import com.planup.planup.goal.adapter.RepenaltyAdapter
import com.planup.planup.goal.adapter.RequestChallengeAdapter
import com.planup.planup.goal.util.toFriendItems
import com.planup.planup.network.RetrofitInstance
import com.planup.planup.network.dto.challenge.ChallengeDto
import com.planup.planup.network.dto.challenge.RepenaltyDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInstance.challengeApi.challengeInfo(challengeId)
                .runCatching {
                    if (isSuccessful && body() != null) {

                    } else if (!isSuccessful && body() != null) {

                    }else {

                    }
                }

        }
    }

    //챌린지 거절
    fun rejectChallenge(challengeId: Int, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch{
            RetrofitInstance.challengeApi.rejectChallenge(challengeId, userId)
                .runCatching {
                    if (isSuccessful && body() != null) {
                        rejectChallengeAdapter.successReject()
                    } else if (!isSuccessful && body() != null) {
                        rejectChallengeAdapter.failReject(body()!!.message)
                    } else {
                        rejectChallengeAdapter.failReject("null")
                    }
                }
        }

    }
    //챌린지  결과 조회
    fun showChallengeResult(userId: Int, challengeId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInstance.challengeApi.challengeResult(userId, challengeId)
                .runCatching {
                    if (isSuccessful && body() != null) {

                    } else if (!isSuccessful && body() != null) {

                    } else {

                    }
                }
        }
    }

    //챌린지 수락
    fun acceptChallenge(challengeId: Int, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInstance.challengeApi.acceptChallenge(challengeId, userId)
                .runCatching {
                    if (isSuccessful && body() != null) {
                        acceptChallengeAdapter.successAccept()
                    } else if (!isSuccessful && body() != null) {
                        acceptChallengeAdapter.failAccept(body()!!.message)
                    } else {
                        acceptChallengeAdapter.failAccept("null")
                    }
                }
        }
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
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInstance.challengeApi.changePenalty(repenaltyDto)
                .runCatching {
                    if (isSuccessful && body() != null) {
                        repenaltyAdapter.successRepenalty()
                    } else if (!isSuccessful && body() != null) {
                        repenaltyAdapter.failRepenalty(body()!!.message)
                    } else {
                        repenaltyAdapter.failRepenalty("null")
                    }
                }
        }
    }

    // 챌린지 생성 요청
    fun requestChallenge(challengeDto: ChallengeDto) {
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInstance.challengeApi.createChallenge(challengeDto)
                .runCatching {
                    if (isSuccess) {
                        requestChallengeAdapter.successRequest()
                    } else {
                        requestChallengeAdapter.failRequest("fail create challenge")
                    }
                }
        }
    }
}