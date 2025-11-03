package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.planup.R
import com.example.planup.component.PlanUpAlertBaseContent
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.friend.ui.FriendReportView
import com.example.planup.main.my.data.BlockedFriend
import com.example.planup.main.my.ui.common.RoutePageDefault
import com.example.planup.main.my.ui.viewmodel.MyPageManageBlockFriendViewModel
import com.example.planup.main.my.ui.viewmodel.UiMessage
import com.example.planup.theme.Blue100
import com.example.planup.theme.Green100
import com.example.planup.theme.Green200
import com.example.planup.theme.Typography


@Composable
fun MyPageManageBlockFriendView(
    onBack: () -> Unit,
    mainSnackbarViewModel: MainSnackbarViewModel,
    myPageManageBlockFriendViewModel: MyPageManageBlockFriendViewModel = hiltViewModel()
) {
    val blockFriendList by myPageManageBlockFriendViewModel.blockFriendList.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        myPageManageBlockFriendViewModel.uiMessage.collect { message ->
            val text = when (message) {
                is UiMessage.Error -> message.msg
                is UiMessage.ReportSuccess -> context.getString(R.string.toast_report)
                is UiMessage.UnBlockSuccess -> {
                    context.getString(R.string.toast_unblock, message.friendName)
                }
            }
            mainSnackbarViewModel.updateErrorMessage(text)
        }
    }
    MyPageManageBlockFriendContent(
        onBack = onBack,
        blockFriendList = listOf(
            BlockedFriend(1, "test1", 0)
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
            BlockedFriend(it + 1, "test${it + 1}", 0)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier.size(42.dp),
                model = blockFriend.profile,
                placeholder = painterResource(R.drawable.profile_image),
                error = painterResource(R.drawable.profile_image),
                contentDescription = null
            )
            Text(
                text = blockFriend.name,
                style = Typography.Medium_XL
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                modifier = Modifier.height(30.dp),
                onClick = {
                    showUnBlockAlert = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green100,
                    contentColor = Green200
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 5.dp
                )
            ) {
                Text(
                    text = "차단 해제",
                    style = Typography.Medium_SM
                )
            }

            Button(
                modifier = Modifier.height(30.dp),
                onClick = {
                    showReportSheet = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue100,
                    contentColor = Color(0xff203358)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 5.dp
                )
            ) {
                Text(
                    text = "신고",
                    style = Typography.Medium_SM
                )
            }
        }
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
        blockFriend = BlockedFriend(1, "test", 0),
        unBlockFriend = {},
        reportFriend = { _, _ -> }
    )
}