package com.example.planup.main.friend.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.databinding.FragmentFriendRequestsBinding
import com.example.planup.main.friend.data.FriendRequest
import com.example.planup.main.friend.adapter.FriendRequestAdapter
import com.example.planup.main.friend.data.FriendActionRequestDto
import com.example.planup.main.friend.data.FriendRequestsResponse
import com.example.planup.main.friend.data.FriendResponseDto
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch
class FriendRequestsFragment : Fragment() {
    lateinit var binding: FragmentFriendRequestsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendRequestsBinding.inflate(inflater, container, false)

        // RecyclerView 레이아웃 매니저 설정
        binding.friendRequestRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchFriendRequests()

        return binding.root
    }

    private fun getAccessToken(): String? {
        val prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }

    private fun fetchFriendRequests() {
        lifecycleScope.launch {
            val token = getAccessToken() ?: return@launch

            try {
                val response = RetrofitInstance.friendApi.getFriendRequests("Bearer $token")

                Log.d("FriendRequests", "status: ${response.code()}, body: ${response.body()}")

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val dtoList = response.body()!!.result
                    val requestList: List<FriendRequest> = dtoList.map { dto ->
                        FriendRequest(
                            id = dto.id,
                            nickname = dto.nickname,
                            status = buildString {
                                append("${dto.goalCnt}개의 목표 진행 중")
                                dto.todayTime?.let { append(" · 오늘 $it") }
                                if (dto.isNewPhotoVerify) append(" · 새 사진 인증")
                            }
                        )
                    }

                    // RecyclerView 어댑터 연결
                    binding.friendRequestRecyclerView.adapter = FriendRequestAdapter(
                        requestList,
                        onAcceptClick = { friend ->
                            acceptFriend(friend) // Fragment에서 API 호출 + 토스트 처리
                        },
                        onDeclineClick = { friend ->
                            declineFriend(friend)
                        }
                    )

                } else {
                    Toast.makeText(requireContext(), "친구 요청을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FriendRequests", "Error: ${e.localizedMessage}")
                Toast.makeText(requireContext(), "오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun acceptFriend(friend: FriendRequest) {
        lifecycleScope.launch {
            val token = getAccessToken() ?: return@launch
            try {
                val response = RetrofitInstance.friendApi.acceptFriendRequest(
                    "Bearer $token",
                    FriendActionRequestDto(friend.id)
                )

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Toast.makeText(requireContext(), "${friend.nickname} 님을 수락했어요.", Toast.LENGTH_SHORT).show()
                    fetchFriendRequests() // 리스트 다시 불러오기
                } else {
                    Toast.makeText(requireContext(), "수락에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun declineFriend(friend: FriendRequest) {
        lifecycleScope.launch {
            val token = getAccessToken() ?: return@launch
            try {
                val response = RetrofitInstance.friendApi.rejectFriendRequest(
                    "Bearer $token",
                    FriendActionRequestDto(friend.id)
                )

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Toast.makeText(requireContext(), "${friend.nickname} 님의 요청을 거절했어요.", Toast.LENGTH_SHORT).show()
                    fetchFriendRequests() // 리스트 다시 불러오기
                } else {
                    Toast.makeText(requireContext(), "거절에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }
}