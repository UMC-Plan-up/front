package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.planup.R
import com.example.planup.component.PlanUpAlertBaseContent
import com.example.planup.component.button.PlanUpSmallButton
import com.example.planup.component.button.SmallButtonType
import com.example.planup.databinding.FragmentFriendListsBinding
import com.example.planup.main.friend.ui.common.FriendDepth2Fragment
import com.example.planup.main.friend.ui.common.FriendProfileRow
import com.example.planup.main.friend.ui.sheet.FriendReportSheet
import com.example.planup.main.friend.ui.viewmodel.FriendUiMessage
import com.example.planup.network.dto.friend.FriendInfo
import kotlinx.coroutines.launch

class FriendListsFragment : FriendDepth2Fragment<FragmentFriendListsBinding>(
    FragmentFriendListsBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            goToFriendMain()
        }
        binding.friendListsRecyclerView
            .setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        binding.friendListsRecyclerView.setContent {
            val friendList by friendViewModel.friendList.collectAsState()
            FriendListView(
                friendList = friendList,
                deleteFriend = { friend ->
                    friendViewModel.deleteFriend(
                        friend = friend
                    )
                },
                blockFriend = { friend ->
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

                        is FriendUiMessage.BlockSuccess -> {
                            mainSnackbarViewModel.updateSuccessMessage(
                                requireContext().getString(
                                    R.string.toast_block,
                                    uiMessage.friendName
                                )
                            )
                        }

                        is FriendUiMessage.DeleteSuccess -> {
                            mainSnackbarViewModel.updateSuccessMessage(
                                requireContext().getString(
                                    R.string.toast_delete,
                                    uiMessage.friendName
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun FriendListView(
    friendList: List<FriendInfo>,
    deleteFriend: (friend: FriendInfo) -> Unit,
    blockFriend: (friend: FriendInfo) -> Unit,
    reportFriend: (friend: FriendInfo, reason: String, withBlock: Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(friendList) { friend ->
            FriendListItem(
                friend = friend,
                deleteFriend = {
                    deleteFriend(friend)
                },
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
    deleteFriend: () -> Unit,
    blockFriend: () -> Unit,
    reportFriend: (reason: String, withBlock: Boolean) -> Unit
) {

    var showDeleteAlert by remember {
        mutableStateOf(false)
    }
    var showBlockAlert by remember {
        mutableStateOf(false)
    }
    var showReportSheet by remember {
        mutableStateOf(false)
    }

    FriendProfileRow(
        profileImage = friend.profileImage,
        friendName = friend.nickname
    ) {
        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Red,
            onClick = {
                showDeleteAlert = true
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
    if (showDeleteAlert) {
        BasicAlertDialog(
            onDismissRequest = {
                showDeleteAlert = false
            },
            properties = DialogProperties()
        ) {
            PlanUpAlertBaseContent(
                title = stringResource(R.string.popup_delete_friend, friend.nickname),
                onDismissRequest = {
                    showDeleteAlert = false
                },
                onConfirm = {
                    deleteFriend()
                    showDeleteAlert = false
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
        deleteFriend = {},
        blockFriend = {},
        reportFriend = { _, _ -> }
    )
}