package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendBinding
import com.example.planup.main.friend.adapter.FriendAdapter
import com.example.planup.main.friend.ui.common.FriendFragmentBase
import kotlinx.coroutines.launch

/**
 * 친구 탭 메인
 */
class FriendFragment : FriendFragmentBase() {

    companion object {
        const val FRIEND_FRAGMENT_STACK = "friend_list"
    }

    private var _binding: FragmentFriendBinding? = null
    private val binding: FragmentFriendBinding
        get() = _binding!!

    private lateinit var friedLAdapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)

        friedLAdapter = FriendAdapter() { friend ->
            goToFriendGoal(
                friend.id,
                friend.nickname
            )
        }

        setupClicks()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        friendViewModel.fetchFriendList()
        friendViewModel.fetchFriendRequest()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        binding.ivNotification.isSelected = pendingCount > 0
    }

    private fun setupClicks() {
        fun goToFriendDepth2(fragment: Fragment) {
            parentFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .addToBackStack(FRIEND_FRAGMENT_STACK)
                .commit()
        }
        binding.ivSetting.setOnClickListener {
            goToFriendDepth2(FriendListsFragment())
        }
        binding.ivNotification.setOnClickListener {
            goToFriendDepth2(FriendRequestsFragment())
        }
        binding.btnAddFriend.setOnClickListener {
            goToFriendDepth2(FriendInviteFragment())
        }
    }
}