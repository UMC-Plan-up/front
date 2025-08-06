package com.example.planup.network.controller

import android.util.Log
import com.example.planup.main.my.data.PostEmail
import com.example.planup.main.my.data.PostNickname
import com.example.planup.main.my.data.PostPasswordChange
import com.example.planup.main.my.ui.ResponseViewer
import com.example.planup.network.adapter.FriendReportAdapter
import com.example.planup.network.adapter.FriendsBlockedAdapter
import com.example.planup.network.adapter.FriendsUnblockedAdapter
import com.example.planup.network.data.BlockedFriends
import com.example.planup.network.data.PostFriendsReport
import com.example.planup.network.data.PostFriendsUnblocked
import com.example.planup.network.entity.FriendReportDto
import com.example.planup.network.entity.FriendUnblockDto
import com.example.planup.network.getRetrofit2
import com.example.planup.network.port.UserControllerInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserController {
    //서버 응답에 따른 마이페이지 관련레이아웃 관리를 위한 변수
    private lateinit var responseViewer: ResponseViewer
    fun setResponseViewer(viewer: ResponseViewer) {
        this.responseViewer = viewer
    }

    //친구 차단에 대한 레이아웃 관리
    private lateinit var friendsBlockedAdapter: FriendsBlockedAdapter
    fun setFriendsBlockedAdapter(adapter: FriendsBlockedAdapter) {
        this.friendsBlockedAdapter = adapter
    }

    //친구 차단 해제에 대한 레이아웃 관리
    private lateinit var friendUnblockedAdapter: FriendsUnblockedAdapter
    fun setFriendUnblockedAdapter(adapter: FriendsUnblockedAdapter){
        this.friendUnblockedAdapter = adapter
    }

    //친구 신고에 대한 레이아웃 관리
    private lateinit var friendReportAdapter: FriendReportAdapter
    fun setFriendReportAdapter(adapter: FriendReportAdapter){
        this.friendReportAdapter = adapter
    }

    //닉네임 변경
    fun nicknameService(userId: Int, nickname: String) {
        val nicknameService = getRetrofit2().create(UserControllerInterface::class.java)
        nicknameService.changeNickname(userId, nickname).enqueue(object : Callback<PostNickname> {
            override fun onResponse(call: Call<PostNickname>, response: Response<PostNickname>) {
                when (response.isSuccessful) {
                    true -> {
                        responseViewer.onResponseSuccess()
                    }

                    else -> {
                        responseViewer.onResponseError(
                            response.code().toString(),
                            response.message().toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<PostNickname>, t: Throwable) {
                Log.d("okhttp", "fail\n$t")
            }

        })
    }

    //이메일 변경
    fun emailService(userId: Int, email: String) {
        val emailService = getRetrofit2().create(UserControllerInterface::class.java)
        emailService.changeEmail(userId, email).enqueue(object : Callback<PostEmail> {
            override fun onResponse(call: Call<PostEmail>, response: Response<PostEmail>) {
                when (response.isSuccessful) {
                    true -> {
                        responseViewer.onResponseSuccess()
                    }

                    else -> {
                        responseViewer.onResponseError(
                            response.code().toString(),
                            response.message().toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<PostEmail>, t: Throwable) {
                Log.d("okhttp", t.toString())
            }

        })
    }

    //비밀번호 변경
    fun passwordService(userId: Int, password: String) {
        val passwordService = getRetrofit2().create(UserControllerInterface::class.java)
        passwordService.changePassword(userId, password)
            .enqueue(object : Callback<PostPasswordChange> {
                override fun onResponse(
                    call: Call<PostPasswordChange>,
                    response: Response<PostPasswordChange>
                ) {
                    when (response.isSuccessful) {
                        true -> {
                            responseViewer.onResponseSuccess()
                        }

                        else -> {
                            responseViewer.onResponseError(
                                response.code().toString(),
                                response.message().toString()
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<PostPasswordChange>, t: Throwable) {
                    Log.d("asfddafa", t.toString())
                }

            })
    }

    //차단 친구 조회
    fun friendsBlockedService(userId: Int) {
        val blockFriendService = getRetrofit2().create(UserControllerInterface::class.java)
        blockFriendService.blockedFriend(userId).enqueue(object : Callback<BlockedFriends> {
            override fun onResponse(
                call: Call<BlockedFriends>,
                response: Response<BlockedFriends>
            ) {
                when (response.isSuccessful) {
                    true -> {
                        friendsBlockedAdapter.successBlockFriend(response.body()?.result)
                    }

                    else -> {
                        friendsBlockedAdapter.failBlockFriend(
                            response.body()!!.code,
                            response.body()!!.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<BlockedFriends>, t: Throwable) {
            }

        })
    }

    //차단 친구 해제
    fun friendsUnblockedService(friendUnblockDto: FriendUnblockDto){
        val friendUnblockService = getRetrofit2().create(UserControllerInterface::class.java)
        friendUnblockService.unblockedFriend(friendUnblockDto).enqueue(object : Callback<PostFriendsUnblocked>{
            override fun onResponse(
                call: Call<PostFriendsUnblocked>,
                response: Response<PostFriendsUnblocked>
            ) {
                when (response.isSuccessful) {
                    true -> {
                        friendUnblockedAdapter.successFriendUnblock(friendUnblockDto.friendNickname)
                    }

                    else -> {
                        friendUnblockedAdapter.failFriendUnblock(response.code().toString(),response.message().toString())
                    }
                }
            }

            override fun onFailure(call: Call<PostFriendsUnblocked>, t: Throwable) {
                Log.d("okhttp",t.toString())
            }

        })
    }

    //친구 신고
    fun reportFriendService(friend: FriendReportDto){
        val reportFriendService = getRetrofit2().create(UserControllerInterface::class.java)
        reportFriendService.reportFriend(friend).enqueue(object : Callback<PostFriendsReport>{
            override fun onResponse(
                call: Call<PostFriendsReport>,
                response: Response<PostFriendsReport>
            ) {
                when (response.isSuccessful){
                    true -> {
                        friendReportAdapter.successReportFriend()
                    }else -> {
                        friendReportAdapter.failReportFriend(response.code().toString(),response.message().toString())
                    }
                }
            }

            override fun onFailure(call: Call<PostFriendsReport>, t: Throwable) {
                Log.d("okhttp",t.toString())
            }

        })
    }
}