package com.example.planup.main.friend.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.component.PlanUpButtonSecondarySmall
import com.example.planup.component.PlanUpButtonSmall
import com.example.planup.databinding.FragmentFriendRequestsBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.friend.adapter.FriendRequestAdapter
import com.example.planup.main.friend.domain.FriendRequestItem
import com.example.planup.main.friend.ui.viewmodel.FriendViewModel
import com.example.planup.network.RetrofitInstance
import com.example.planup.network.dto.friend.FriendRequest
import kotlinx.coroutines.launch

class FriendRequestsFragment : Fragment() {
    lateinit var binding: FragmentFriendRequestsBinding

    private val friendViewModel : FriendViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        binding.friendRequestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                friendViewModel.friendRequestList.collect { dtoList ->
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
                        onAcceptClick = { friend ->  },
                        onDeclineClick = { friend ->  }
                    )
                }
            }
        }
    }
//
//    private fun acceptFriend(friend: FriendRequest) {
//        lifecycleScope.launch {
//            val auth = buildAuthHeader() ?: return@launch
//            try {
//                val resp = RetrofitInstance.friendApi.acceptFriendRequest(auth, friend.id) // ✅ 정수 전달
//                Log.d("FriendAccept", "code=${resp.code()}, body=${resp.body()}, err=${resp.errorBody()?.string()}")
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    Toast.makeText(requireContext(), "${friend.nickname} 님을 수락했어요.", Toast.LENGTH_SHORT).show()
//                    fetchFriendRequests()
//                } else {
//                    val msg = resp.body()?.message ?: resp.errorBody()?.string() ?: "수락에 실패했습니다."
//
//                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun declineFriend(friend: FriendRequest) {
//        lifecycleScope.launch {
//            val auth = buildAuthHeader() ?: return@launch
//            try {
//                val resp = RetrofitInstance.friendApi.rejectFriendRequest(auth, friend.id) // ✅ 정수 전달
//                Log.d("FriendReject", "code=${resp.code()}, body=${resp.body()}, err=${resp.errorBody()?.string()}")
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    Toast.makeText(requireContext(), "${friend.nickname} 님의 요청을 거절했어요.", Toast.LENGTH_SHORT).show()
//                    fetchFriendRequests()
//                } else {
//                    val msg = resp.body()?.message ?: resp.errorBody()?.string() ?: "거절에 실패했습니다."
//                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}

@Composable
fun FriendRequestItem(
    friendInfo: FriendRequestItem,
    clickItem: () -> Unit,
    acceptFriend: () -> Unit,
    declineFriend: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.clickable {
                clickItem()
            },
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp)
            ) { }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = friendInfo.nickName
                    )
                    Text(
                        text = friendInfo.count.toString()
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlanUpButtonSmall(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                title = "수락",
                onClick = acceptFriend
            )
            PlanUpButtonSecondarySmall(
                modifier = Modifier.weight(1f),
                title = "거절",
                onClick = declineFriend
            )
        }
    }
}

@Composable
@Preview
private fun FriendRequestItemPreview() {
    FriendRequestItem(
        FriendRequestItem(
            id = 1,
            nickName = "Tester",
            1
        ),
        {},
        {},
        {}
    )
}