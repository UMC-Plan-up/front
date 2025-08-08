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
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendBinding
import com.example.planup.databinding.FragmentFriendListsBinding
import com.example.planup.main.friend.adapter.FriendAdapter
import com.example.planup.main.friend.adapter.FriendListsAdapter
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendFragment : Fragment() {
    lateinit var binding: FragmentFriendBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendBinding.inflate(inflater, container, false)
        fetchData()
        clickListener()
        return binding.root
    }

    fun getAccessToken(): String? {
        val prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }

    private fun fetchData(){
        lifecycleScope.launch {
            try {
                val token = getAccessToken()
                if (token == null) {
                    Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val response = RetrofitInstance.friendApi.getFriendSummary("Bearer $token")

                Log.d("FriendFragment", "HTTP Status Code: ${response.code()}")
                Log.d("FriendFragment", "Raw Response: $response")
                Log.d("FriendFragment", "Body: ${response.body()}")

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val friendList = response.body()!!.result.first().friendInfoSummaryList
                    binding.rvFriendList.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvFriendList.adapter = FriendAdapter(friendList)
                } else {
                    Toast.makeText(requireContext(), "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                Log.e("FriendFragment", "Error fetching data", e)
            }
        }
    }

    private fun clickListener(){
        binding.ivSetting.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendListsFragment())
                .commitAllowingStateLoss()
        }

        binding.ivNotification.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendRequestsFragment())
                .commitAllowingStateLoss()
        }

        binding.btnAddFriend.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendInviteFragment())
                .commitAllowingStateLoss()
        }
    }
}