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
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendRequestsBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.friend.adapter.FriendRequestAdapter
import com.example.planup.network.RetrofitInstance
import com.example.planup.network.dto.friend.FriendRequest
import kotlinx.coroutines.launch

class FriendRequestsFragment : Fragment() {
    lateinit var binding: FragmentFriendRequestsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        binding.friendRequestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchFriendRequests()
        clickListener()
        return binding.root
    }

    private fun buildAuthHeader(): String? {
        val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val prefsToken = prefs.getString("accessToken", null)
        val appToken = com.example.planup.network.App.jwt.token

        val raw = when {
            !prefsToken.isNullOrBlank() -> prefsToken
            !appToken.isNullOrBlank() -> appToken
            else -> null
        } ?: return null

        Log.d("Auth", "token? ${!raw.isNullOrBlank()} startsWithBearer=${raw.startsWith("Bearer", true)}")
        return if (raw.startsWith("Bearer ", ignoreCase = true)) raw else "Bearer $raw"
    }

    private fun fetchFriendRequests() {
        lifecycleScope.launch {
            val auth = buildAuthHeader()
            if (auth.isNullOrBlank()) {
                Toast.makeText(requireContext(), "로그인이 필요합니다(토큰 없음).", Toast.LENGTH_SHORT).show()
                Log.e("FriendAction", "No Authorization token")
                return@launch
            }

            try {
                val response = RetrofitInstance.friendApi.getFriendRequests(auth)
                Log.d("FriendRequests", "status: ${response.code()}, body: ${response.body()}")

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val dtoList = response.body()!!.result

                    val items: List<FriendRequest> = dtoList.map { dto ->
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

                    binding.friendRequestRecyclerView.adapter = FriendRequestAdapter(
                        items,
                        onAcceptClick = { friend -> acceptFriend(friend) },
                        onDeclineClick = { friend -> declineFriend(friend) }
                    )
                } else {
                    Toast.makeText(requireContext(), response.body()?.message ?: "친구 요청을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FriendRequests", "Error: ${e.localizedMessage}", e)
                Toast.makeText(requireContext(), "오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun acceptFriend(friend: FriendRequest) {
        lifecycleScope.launch {
            val auth = buildAuthHeader() ?: return@launch
            try {
                val resp = RetrofitInstance.friendApi.acceptFriendRequest(auth, friend.id) // ✅ 정수 전달
                Log.d("FriendAccept", "code=${resp.code()}, body=${resp.body()}, err=${resp.errorBody()?.string()}")
                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                    Toast.makeText(requireContext(), "${friend.nickname} 님을 수락했어요.", Toast.LENGTH_SHORT).show()
                    fetchFriendRequests()
                } else {
                    val msg = resp.body()?.message ?: resp.errorBody()?.string() ?: "수락에 실패했습니다."

                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun declineFriend(friend: FriendRequest) {
        lifecycleScope.launch {
            val auth = buildAuthHeader() ?: return@launch
            try {
                val resp = RetrofitInstance.friendApi.rejectFriendRequest(auth, friend.id) // ✅ 정수 전달
                Log.d("FriendReject", "code=${resp.code()}, body=${resp.body()}, err=${resp.errorBody()?.string()}")
                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                    Toast.makeText(requireContext(), "${friend.nickname} 님의 요청을 거절했어요.", Toast.LENGTH_SHORT).show()
                    fetchFriendRequests()
                } else {
                    val msg = resp.body()?.message ?: resp.errorBody()?.string() ?: "거절에 실패했습니다."
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clickListener(){
        binding.btnBack.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendFragment())
                .commitAllowingStateLoss()
        }
    }
}