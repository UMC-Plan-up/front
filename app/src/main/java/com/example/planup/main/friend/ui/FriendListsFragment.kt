package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.compose.content
import com.example.planup.R
import com.example.planup.component.PageDefault
import com.example.planup.component.PlanUpAlertBaseContent
import com.example.planup.component.TopHeader
import com.example.planup.component.button.PlanUpSmallButton
import com.example.planup.component.button.SmallButtonType
import com.example.planup.main.friend.ui.common.FriendDepth2FragmentBase
import com.example.planup.main.friend.ui.common.FriendProfileRow
import com.example.planup.main.friend.ui.sheet.FriendReportSheet
import com.example.planup.network.dto.friend.FriendInfo
import com.example.planup.theme.Black400
import com.example.planup.theme.Typography

class FriendListsFragment : FriendDepth2FragmentBase() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return content {
            val friendList by friendViewModel.friendList.collectAsState()

            PageDefault {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                ) {
                    TopHeader(
                        modifier = Modifier
                            .height(32.dp),
                        onBackAction = ::goToFriendMain,
                        title = stringResource(R.string.friend_title)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        modifier = Modifier
                            .height(30.dp),
                        text = stringResource(R.string.friend_list_title),
                        style = Typography.Medium_XL,
                        color = Black400
                    )
                }
                FriendListView(
                    friendList = friendList,
                    deleteFriend = friendViewModel::deleteFriend,
                    blockFriend = friendViewModel::blockFriend,
                    reportFriend = friendViewModel::reportFriend
                )
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
        modifier = Modifier.padding(horizontal = 20.dp),
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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