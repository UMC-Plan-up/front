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
import com.example.planup.databinding.FragmentFriendBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.friend.adapter.FriendAdapter
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {
    lateinit var binding: FragmentFriendBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendBinding.inflate(inflater, container, false)
        fetchData()
        clickListener()
        return binding.root
    }

    /** Authorization 헤더 생성: userInfo prefs 우선, 없으면 App.jwt.token 폴백 */
    private fun buildAuthHeader(): String? {
        val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val prefsToken = prefs.getString("accessToken", null)
        val appToken = com.example.planup.network.App.jwt.token

        val raw = when {
            !prefsToken.isNullOrBlank() -> prefsToken
            !appToken.isNullOrBlank() -> appToken
            else -> null
        } ?: return null

        return if (raw.startsWith("Bearer ", ignoreCase = true)) raw else "Bearer $raw"
    }

    private fun fetchData() {
        lifecycleScope.launch {
            val auth = buildAuthHeader()
            if (auth.isNullOrBlank()) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val response = RetrofitInstance.friendApi.getFriendSummary(auth)

                Log.d("FriendFragment", "HTTP ${response.code()}")
                Log.d("FriendFragment", "Body: ${response.body()}")

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val resultList = response.body()!!.result
                    val friendList = resultList.firstOrNull()?.friendInfoSummaryList.orEmpty()

                    binding.rvFriendList.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvFriendList.adapter = FriendAdapter(friendList)
                } else {
                    Toast.makeText(requireContext(), response.body()?.message ?: "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FriendFragment", "Error fetching data", e)
                Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clickListener() {
        binding.ivSetting.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendListsFragment())
                .commitAllowingStateLoss()
        }

        binding.ivNotification.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendRequestsFragment())
                .commitAllowingStateLoss()
        }

        binding.btnAddFriend.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendInviteFragment())
                .commitAllowingStateLoss()
        }
    }
}