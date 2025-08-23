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
import com.example.planup.main.goal.ui.GoalFragment
import com.example.planup.main.home.ui.FriendGoalListFragment
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {
    private lateinit var binding: FragmentFriendBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendBinding.inflate(inflater, container, false)
        setupClicks()
        fetchAll()
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

    /** 알림 배지 표시/숨김 */
    private fun updateNotificationBadge(pendingCount: Int) {
        // 요청이 1개 이상이면 빨간 점 표시, 아니면 숨김
        binding.notificationBadge.visibility =
            if (pendingCount > 0) View.VISIBLE else View.GONE
    }

    /** 친구 요약 + 대기중 초대요청 수 모두 로드 */
    private fun fetchAll() {
        lifecycleScope.launch {
            val auth = buildAuthHeader()
            if (auth.isNullOrBlank()) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // 1) 친구 요약 불러오기
            runCatching { RetrofitInstance.friendApi.getFriendSummary(auth) }
                .onSuccess { resp ->
                    Log.d("FriendFragment", "summary HTTP ${resp.code()}")
                    if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                        val resultList = resp.body()!!.result
                        val friendList = resultList.firstOrNull()?.friendInfoSummaryList.orEmpty()

                        binding.tvFriendCount.text = "목록 (${friendList.size}명)"
                        binding.rvFriendList.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = FriendAdapter(friendList) { friend ->
                                // ▶ 친구 목표 보기로 이동
//                                val frag = GoalFragment.newInstance(
//                                    targetUserId = friend.id,
//                                    targetNickname = friend.nickname
//                                )
                                val frag = GoalFragment()
                                (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                                    .replace(R.id.main_container, frag)
                                    .addToBackStack(null)
                                    .commitAllowingStateLoss()
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            resp.body()?.message ?: "데이터를 불러오지 못했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .onFailure {
                    Log.e("FriendFragment", "summary error", it)
                    Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                }

            // 2) 대기중 친구요청 수로 배지 갱신
            runCatching { RetrofitInstance.friendApi.getFriendRequests(auth) }
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                        val pendingCount = resp.body()!!.result.size
                        updateNotificationBadge(pendingCount)
                    } else {
                        updateNotificationBadge(0)
                    }
                }
                .onFailure {
                    updateNotificationBadge(0)
                }
        }
    }

    private fun setupClicks() {
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