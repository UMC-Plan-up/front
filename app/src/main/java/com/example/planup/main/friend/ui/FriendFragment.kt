package com.example.planup.main.friend.ui

import android.os.Bundle
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
import com.example.planup.network.ApiResult
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                friendViewModel.friendRequestList.collect { friendList ->
                    updateNotificationBadge(friendList.size)
                }
            }
        }
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

            // 1) 친구 요약 불러오기
            friendViewModel.fetchFriendList(
                onCallBack = { friendResult ->
                    when (friendResult) {
                        is ApiResult.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "데이터를 불러오지 못했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ApiResult.Exception -> {
                            Toast.makeText(
                                requireContext(),
                                "데이터를 불러오지 못했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ApiResult.Fail -> {
                            Toast.makeText(
                                requireContext(),
                                friendResult.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }
                }
            )

            // 2) 대기중 친구요청 수로 배지 갱신
            friendViewModel.fetchFriendRequest(
                onCallBack = {

                }
            )
        }
    }

    private fun setupClicks() {
        binding.ivSetting.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendListsFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
        binding.ivNotification.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendRequestsFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
        binding.btnAddFriend.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendInviteFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}