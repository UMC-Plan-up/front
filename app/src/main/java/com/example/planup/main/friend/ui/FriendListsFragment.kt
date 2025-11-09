package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.planup.R
import com.example.planup.component.PlanUpAlertBaseContent
import com.example.planup.component.button.PlanUpSmallButton
import com.example.planup.component.button.SmallButtonType
import com.example.planup.databinding.FragmentFriendListsBinding
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.friend.ui.common.FriendProfileRow
import com.example.planup.main.friend.ui.sheet.FriendReportSheet
import com.example.planup.main.friend.ui.viewmodel.FriendUiMessage
import com.example.planup.main.friend.ui.viewmodel.FriendViewModel
import com.example.planup.network.dto.friend.FriendInfo
import kotlinx.coroutines.launch

class FriendListsFragment : Fragment() {
    private var _binding: FragmentFriendListsBinding? = null
    val binding: FragmentFriendListsBinding
        get() = _binding!!

    private val friendViewModel: FriendViewModel by activityViewModels()
    private val mainSnackbarViewModel: MainSnackbarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack(
                FriendFragment.FRIEND_FRAGMENT_STACK,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
        binding.friendListsRecyclerView.setContent {
            val friendList by friendViewModel.friendList.collectAsState()
            FriendListView(
                friendList = friendList,
                blockFriend =  { friend ->
                    friendViewModel.blockFriend(
                        friend = friend
                    )
                },
                reportFriend = { friend, reason, withBlock ->
                    friendViewModel.reportFriend(
                        friend = friend,
                        reason = reason,
                        withBlock = withBlock
                    )
                }
            )
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                friendViewModel.uiMessage.collect { uiMessage ->
                    when (uiMessage) {
                        is FriendUiMessage.Error -> {
                            mainSnackbarViewModel.updateErrorMessage(
                                uiMessage.msg
                            )
                        }

                        is FriendUiMessage.ReportSuccess -> {
                            mainSnackbarViewModel.updateSuccessMessage(
                                requireContext().getString(R.string.toast_report)
                            )
                        }

                        is FriendUiMessage.BlockSuccess ->  {
                            mainSnackbarViewModel.updateSuccessMessage(
                                requireContext().getString(R.string.toast_block,uiMessage.friendName)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //    /** 친구 목록 조회 */
//    private fun fetchFriends() {
//        lifecycleScope.launch {
//            val auth = buildAuthHeader()
//            if (auth.isNullOrBlank()) {
//                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
//                return@launch
//            }
//
//            try {
//                val resp = RetrofitInstance.friendApi.getFriendSummary(auth)
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    val list: List<FriendInfo> =
//                        resp.body()!!.result.firstOrNull()?.friendInfoSummaryList.orEmpty()
//
//                    binding.friendListsRecyclerView.adapter = FriendListsAdapter(
//                        items = list,
//                        onDeleteClick = { friend -> deleteDialog(friend) },
//                        onBlockClick = { friend -> blockDialog(friend) },
//                        onReportClick = { friend -> showReportDialog(friend) }
//                    )
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        resp.body()?.message ?: "목록을 불러오지 못했습니다.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    /** “삭제” 동작
//     * 서버에 ‘삭제’ API가 별도로 없다면, 실사용에선 ‘차단’으로 대체하거나
//     * 백엔드에 “언프렌드” 엔드포인트를 요청해야 합니다.
//     * 여기서는 편의상 ‘차단’과 동일하게 처리합니다.
//     */
//    /** 삭제 API **/
//    private fun deleteFriend(friend: FriendInfo) {
//        lifecycleScope.launch {
//            val auth = buildAuthHeader() ?: return@launch
//            try {
//                val resp = RetrofitInstance.friendApi.deleteFriend(
//                    token = auth,
//                    request = friend.id
//                )
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    Toast.makeText(
//                        requireContext(),
//                        "${friend.nickname} 님을 삭제했어요.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    fetchFriends() // 갱신
//                } else {
//                    Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun deleteDialog(friend: FriendInfo) {
//        val dialog = Dialog(context as MainActivity)
//        dialog.setContentView(R.layout.dialog_delete_friend)
//        dialog.window?.apply {
//            setGravity(Gravity.CENTER)
//            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent))
//            dialog.setCanceledOnTouchOutside(true)
//        }
//        dialog.findViewById<TextView>(R.id.popup_delete_title_tv).text =
//            getString(R.string.popup_delete_friend, friend.nickname)
//        dialog.findViewById<TextView>(R.id.popup_friend_no_btn).setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.findViewById<TextView>(R.id.popup_friend_yes_btn).setOnClickListener {
//            lifecycleScope.launch {
//                val auth = buildAuthHeader() ?: return@launch
//                try {
//                    val resp = RetrofitInstance.friendApi.deleteFriend(
//                        token = auth,
//                        request = friend.id
//                    )
//                    if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                        fetchFriends() // 갱신
//
//                        val inflater = LayoutInflater.from(context)
//                        val layout = inflater.inflate(R.layout.toast_grey_template, null)
//                        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text =
//                            getString(R.string.toast_delete, friend.nickname)
//
//                        val toast = Toast(context)
//                        toast.view = layout
//                        toast.duration = LENGTH_SHORT
//                        toast.setGravity(Gravity.BOTTOM, 0, 300)
//                        toast.show()
//                    } else {
//                        Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//                }
//            }
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
}


@Composable
private fun FriendListView(
    friendList: List<FriendInfo>,
    blockFriend: (friend: FriendInfo) -> Unit,
    reportFriend: (friend: FriendInfo, reason: String, withBlock: Boolean) -> Unit
) {
    LazyColumn(

    ) {
        items(friendList) { friend ->
            FriendListItem(
                friend = friend,
                blockFriend = {
                    blockFriend(friend)
                },
                reportFriend = { reason, withBlock ->
                    reportFriend(friend, reason, withBlock)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendListItem(
    friend: FriendInfo,
    blockFriend: () -> Unit,
    reportFriend: (reason: String, withBlock: Boolean) -> Unit
) {
    var showReportSheet by remember {
        mutableStateOf(false)
    }

    var showBlockAlert by remember {
        mutableStateOf(false)
    }

    FriendProfileRow(
        profileImage = friend.profileImage,
        friendName = friend.nickname
    ) {
        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Red,
            onClick = {

            },
            title = "삭제"
        )

        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Green,
            onClick = {
                showBlockAlert = true
            },
            title = "차단"
        )

        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Blue,
            onClick = {
                showReportSheet = true
            },
            title = "신고"
        )
    }
    if (showReportSheet) {
        FriendReportSheet(
            showWithBlock = true,
            onDismissRequest = {
                showReportSheet = false
            },
            reportFriend = { reason, withBlock ->
                reportFriend(reason, withBlock)
            }
        )
    }
    if (showBlockAlert) {
        BasicAlertDialog(
            onDismissRequest = {
                showBlockAlert = false
            },
            properties = DialogProperties()
        ) {
            PlanUpAlertBaseContent(
                title = stringResource(R.string.popup_block_friend_title, friend.nickname),
                subTitle = stringResource(R.string.popup_block_friend_explain),
                onDismissRequest = {
                    showBlockAlert = false
                },
                onConfirm = {
                    blockFriend()
                    showBlockAlert = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendListItemPreview() {
    FriendListItem(
        friend = FriendInfo(
            1,
            "test",
            1,
            "",
            true,
            ""
        ),
        blockFriend = {},
        reportFriend = { _, _ -> }
    )
}