package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.R
import com.example.planup.component.PlanUpAlertBaseContent
import com.example.planup.component.RoutePageDefault
import com.example.planup.component.button.PlanUpSmallButton
import com.example.planup.component.button.SmallButtonType
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.friend.ui.FriendReportView
import com.example.planup.main.friend.ui.common.FriendProfileRow
import com.example.planup.main.my.data.BlockedFriend
import com.example.planup.main.my.ui.viewmodel.MyPageManageBlockFriendViewModel
import com.example.planup.main.my.ui.viewmodel.UiMessage


@Composable
fun MyPageManageBlockFriendView(
    onBack: () -> Unit,
    mainSnackbarViewModel: MainSnackbarViewModel,
    myPageManageBlockFriendViewModel: MyPageManageBlockFriendViewModel = hiltViewModel()
) {
    val blockFriendList by myPageManageBlockFriendViewModel.blockFriendList.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        myPageManageBlockFriendViewModel.fetchBlockFriend()
    }
    LaunchedEffect(Unit) {
        myPageManageBlockFriendViewModel.uiMessage.collect { message ->
            when (message) {
                is UiMessage.Error -> {
                    mainSnackbarViewModel.updateErrorMessage(message.msg)
                }

                is UiMessage.ReportSuccess -> {
                    mainSnackbarViewModel.updateSuccessMessage(
                        context.getString(R.string.toast_report)
                    )
                }

                is UiMessage.UnBlockSuccess -> {
                    mainSnackbarViewModel.updateSuccessMessage(
                        context.getString(R.string.toast_unblock, message.friendName)
                    )
                }
            }
        }
    }
    MyPageManageBlockFriendContent(
        onBack = onBack,
        blockFriendList = listOf(
            BlockedFriend(1, "test1", "")
        ) + blockFriendList,
        unBlockFriend = { friend ->
            myPageManageBlockFriendViewModel.unBlockFriend(
                friend = friend
            )
        },
        reportFriend = { friend, reason, withBlock ->
            myPageManageBlockFriendViewModel.reportFriend(
                friend = friend,
                reason = reason,
                withBlock = withBlock
            )
        }
    )
}

@Composable
fun MyPageManageBlockFriendContent(
    onBack: () -> Unit,
    blockFriendList: List<BlockedFriend>,
    unBlockFriend: (friend: BlockedFriend) -> Unit,
    reportFriend: (friend: BlockedFriend, reason: String, withBlock: Boolean) -> Unit
) {
    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_block)
    ) {
        LazyColumn {
            blockFriendList.forEach { blockedFriend ->
                item {
                    FriendBlockItem(
                        blockFriend = blockedFriend,
                        unBlockFriend = {
                            unBlockFriend(blockedFriend)
                        },
                        reportFriend = { reason, withBlock ->
                            reportFriend(blockedFriend, reason, withBlock)
                        }
                    )
                }
                item {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPageManageBlockFriendContentPreview() {
    MyPageManageBlockFriendContent(
        onBack = {},
        blockFriendList = List(100) {
            BlockedFriend(it + 1, "test${it + 1}", "")
        },
        unBlockFriend = {},
        reportFriend = { _, _, _ -> }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendBlockItem(
    blockFriend: BlockedFriend,
    unBlockFriend: () -> Unit,
    reportFriend: (reason: String, withBlock: Boolean) -> Unit
) {
    var showUnBlockAlert by remember {
        mutableStateOf(false)
    }
    var showReportSheet by remember {
        mutableStateOf(false)
    }
    FriendProfileRow(
        profileImage = blockFriend.profile,
        friendName = blockFriend.name
    ) {
        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Green,
            onClick = {
                showUnBlockAlert = true
            },
            title = "차단 해제"
        )

        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Blue,
            onClick = {
                showReportSheet = true
            },
            title = "신고"
        )
    }
    if (showUnBlockAlert) {
        BasicAlertDialog(
            onDismissRequest = {
                showUnBlockAlert = false
            },
            properties = DialogProperties()
        ) {
            PlanUpAlertBaseContent(
                title = stringResource(R.string.popup_unblock_title, blockFriend.name),
                subTitle = stringResource(R.string.popup_unblock_explain),
                onDismissRequest = {
                    showUnBlockAlert = false
                },
                onConfirm = {
                    unBlockFriend()
                    showUnBlockAlert = false
                }
            )
        }
    }
    if (showReportSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth(0.95f),
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            containerColor = Color.White,
            onDismissRequest = {
                showReportSheet = false
            }
        ) {
            FriendReportView(
                report = { reason, withBlock ->
                    reportFriend(reason, withBlock)
                    showReportSheet = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendBlockItemPreview() {
    FriendBlockItem(
        blockFriend = BlockedFriend(1, "test", ""),
        unBlockFriend = {},
        reportFriend = { _, _ -> }
    )
}