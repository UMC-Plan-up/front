package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.compose.content
import com.example.planup.R
import com.example.planup.component.CircleProfileImageView
import com.example.planup.component.PageDefault
import com.example.planup.component.PlanUpButtonSecondarySmall
import com.example.planup.component.PlanUpButtonSmall
import com.example.planup.component.TopHeader
import com.example.planup.main.friend.ui.common.FriendDepth2FragmentBase
import com.example.planup.network.dto.friend.FriendInfo
import com.example.planup.theme.Black400
import com.example.planup.theme.Typography

class FriendRequestsFragment : FriendDepth2FragmentBase() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return content {
            val friendRequestList by friendViewModel.friendRequestList.collectAsState()
            PageDefault() {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                ) {
                    TopHeader(
                        modifier = Modifier
                            .height(36.dp),
                        onBackAction = ::goToFriendMain,
                        textStyle = Typography.Semibold_3XL,
                        title = stringResource(R.string.friend_title)
                    )
                    Text(
                        modifier = Modifier
                            .height(30.dp),
                        text = stringResource(R.string.friend_request_title),
                        style = Typography.Medium_XL,
                        color = Black400
                    )
                }
                Spacer(Modifier.height(24.dp))
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(
                        items = friendRequestList,
                        key = FriendInfo::id
                    ) {friendRequestItem ->
                        FriendRequestItem(
                            friendInfo = friendRequestItem,
                            clickItem = {
                                goToFriendGoal(
                                    friendId = friendRequestItem.id,
                                    friendName = friendRequestItem.nickname
                                )
                            },
                            acceptFriend = {
                                friendViewModel.acceptFriendRequest(
                                    friendRequestItem.id,
                                    friendRequestItem.nickname
                                )
                            },
                            declineFriend = {
                                friendViewModel.declineFriendRequest(
                                    friendRequestItem.id,
                                    friendRequestItem.nickname
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendRequestItem(
    friendInfo: FriendInfo,
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
            ) {
                CircleProfileImageView(
                    modifier = Modifier.size(42.dp)
                        .align(Alignment.Center),
                    profileImage = friendInfo.profileImage
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = friendInfo.nickname
                    )
                    Text(
                        text = friendInfo.getStatusString()
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
                title = stringResource(R.string.btn_approval),
                onClick = acceptFriend
            )
            PlanUpButtonSecondarySmall(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.btn_rejection),
                onClick = declineFriend
            )
        }
    }
}

@Composable
@Preview
private fun FriendRequestItemPreview() {
    FriendRequestItem(
        FriendInfo(
            id = 1,
            nickname = "Tester",
            goalCnt = 1,
            todayTime = "",
            isNewPhotoVerify = true,
            profileImage = null
        ),
        {},
        {},
        {}
    )
}