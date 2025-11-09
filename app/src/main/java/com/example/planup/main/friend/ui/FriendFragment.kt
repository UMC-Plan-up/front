package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.launch

/**
 * 친구 탭 메인
 */
class FriendFragment : Fragment() {

    companion object {
        const val FRIEND_FRAGMENT_STACK = "friend_list"
    }
    private var _binding: FragmentFriendBinding? = null
    private val binding: FragmentFriendBinding
        get() = _binding!!

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
        _binding = FragmentFriendBinding.inflate(inflater, container, false)

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
        binding.notificationBadge.visibility =
            if (pendingCount > 0) View.VISIBLE else View.GONE
    }

    private fun setupClicks() {
        binding.ivSetting.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.main_container, FriendListsFragment())
                .addToBackStack(FRIEND_FRAGMENT_STACK)
                .commit()
        }
        binding.ivNotification.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.main_container, FriendRequestsFragment())
                .addToBackStack(FRIEND_FRAGMENT_STACK)
                .commit()
        }
        binding.btnAddFriend.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.main_container, FriendInviteFragment())
                .addToBackStack(FRIEND_FRAGMENT_STACK)
                .commit()
        }
    }
}