package com.example.planup.main.friend.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.friend.adapter.FriendAdapter
import com.example.planup.main.friend.ui.viewmodel.FriendViewModel
import com.example.planup.main.goal.ui.GoalFragment
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * 친구 탭 메인
 */
class FriendFragment : Fragment() {
    private lateinit var binding: FragmentFriendBinding

    /**
     * Hilt를 통해 [FriendViewModel]이 사용됨
     */
    private val friendViewModel: FriendViewModel by activityViewModels()

    private lateinit var friedLAdapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendBinding.inflate(inflater, container, false)

        friedLAdapter = FriendAdapter() { friend ->
            // ▶ 친구 목표 보기로 이동
            val gaolFragment = GoalFragment.newInstance(
                targetUserId = friend.id,
                targetNickname = friend.nickname
            )

            // val frag = GoalFragment()
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, gaolFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        setupClicks()
        fetchAll()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFriendList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friedLAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                friendViewModel.friendList.collect { friendList ->
                    binding.tvFriendCount.text = "목록 (${friendList.size}명)"
                    friedLAdapter.setItems(friendList)
                }
            }
        }
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
            friendViewModel.fetchFriendList(
                onSuccess = { friendList ->
                    if (friendList == null) {
                        Toast.makeText(
                            requireContext(),
//                            resp.body()?.message ?:
                            "데이터를 불러오지 못했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onError = {
                    Log.e("FriendFragment", "summary error", it)
                    Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
                }
            )

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