package com.example.planup.network.controller

import android.util.Log
import com.example.planup.network.adapter.FriendReportAdapter
import com.example.planup.network.adapter.FriendsBlockedAdapter
import com.example.planup.network.adapter.FriendsUnblockedAdapter
import com.example.planup.network.data.BlockedFriends
import com.example.planup.network.data.FriendResponse
import com.example.planup.network.dto.friend.FriendReportDto
import com.example.planup.network.dto.friend.FriendUnblockDto
import com.example.planup.network.getRetrofit
import com.example.planup.network.port.FriendPort
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendController {

    /*
* Adapter는 각 API 서비스 응답에 대한 레이아웃 변화를 관리함
* 레이아웃 관리하는 .kt 파일에서 해당 인터페이스를 구현하여 API 응답 반영함*/

    //친구 차단에 대한 레이아웃 관리
    private lateinit var friendsBlockedAdapter: FriendsBlockedAdapter
    fun setFriendsBlockedAdapter(adapter: FriendsBlockedAdapter) {
        this.friendsBlockedAdapter = adapter
    }

    //친구 차단 해제에 대한 레이아웃 관리
    private lateinit var friendUnblockedAdapter: FriendsUnblockedAdapter
    fun setFriendUnblockedAdapter(adapter: FriendsUnblockedAdapter) {
        this.friendUnblockedAdapter = adapter
    }

    //친구 신고에 대한 레이아웃 관리
    private lateinit var friendReportAdapter: FriendReportAdapter
    fun setFriendReportAdapter(adapter: FriendReportAdapter) {
        this.friendReportAdapter = adapter
    }

    //차단 친구 목록 조회
    fun friendsBlockedService() {
        val blockFriendService = getRetrofit().create(FriendPort::class.java)
        blockFriendService.blockedFriends()
            .enqueue(object : Callback<FriendResponse<List<BlockedFriends>>> {
                override fun onResponse(
                    call: Call<FriendResponse<List<BlockedFriends>>>,
                    response: Response<FriendResponse<List<BlockedFriends>>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        friendsBlockedAdapter.successBlockedFriends(response.body()!!.result)
                    } else {
                        friendsBlockedAdapter.failBlockedFriends(
                            response.body()?.code, response.body()?.message
                        )
                    }
                }

                override fun onFailure(
                    call: Call<FriendResponse<List<BlockedFriends>>>, t: Throwable
                ) {
                    Log.d("okhttp", "fail\n$t")
                }

            })
    }

    //친구 차단 해제
    fun friendsUnblockService(friendUnblockDto: FriendUnblockDto) {
        val friendUnblockService = getRetrofit().create(FriendPort::class.java)
        friendUnblockService.unblockFriend(friendUnblockDto)
            .enqueue(object : Callback<FriendResponse<Boolean>> {
                override fun onResponse(
                    call: Call<FriendResponse<Boolean>>, response: Response<FriendResponse<Boolean>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("okhttp", "success ${response.body()}")
                        friendUnblockedAdapter.successFriendUnblock(friendUnblockDto.friendNickname)
                    } else {
                        friendUnblockedAdapter.failFriendUnblock(
                            response.code().toString(), response.message().toString()
                        )
                    }
                }

                override fun onFailure(call: Call<FriendResponse<Boolean>>, t: Throwable) {
                    Log.d("okhttp", "fail\n$t")
                }

            })
    }

    //친구 신고
    fun reportFriendService(friend: FriendReportDto) {
        val reportFriendService = getRetrofit().create(FriendPort::class.java)
        reportFriendService.reportFriend(friend)
            .enqueue(object : Callback<FriendResponse<Boolean>> {
                override fun onResponse(
                    call: Call<FriendResponse<Boolean>>, response: Response<FriendResponse<Boolean>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        friendReportAdapter.successReportFriend()
                    } else {
                        friendReportAdapter.failReportFriend(
                            response.code().toString(), response.message().toString()
                        )
                    }
                }

                override fun onFailure(call: Call<FriendResponse<Boolean>>, t: Throwable) {
                    Log.d("okhttp", t.toString())
                }

            })
    }
}