package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import com.example.planup.R
import com.example.planup.component.CircleProfileImageView
import com.example.planup.component.PageDefault
import com.example.planup.component.TopHeader
import com.example.planup.main.friend.ui.common.FriendFragmentBase
import com.example.planup.network.dto.friend.FriendInfo
import com.example.planup.theme.Black300
import com.example.planup.theme.Blue100
import com.example.planup.theme.Blue200
import com.example.planup.theme.Typography
import kotlinx.serialization.json.JsonNull.content

/**
 * 친구 탭 메인
 */
class FriendFragment : FriendFragmentBase() {

    companion object {
        const val FRIEND_FRAGMENT_STACK = "friend_list"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return content {
            val friendList by friendViewModel.friendList.collectAsState()
            val friendRequestList by friendViewModel.friendRequestList.collectAsState(emptyList())

            val showBadge by remember(friendRequestList) {
                derivedStateOf { friendRequestList.isNotEmpty() }
            }
            FriendHomeView(
                goRequest = {
                    goToFriendDepth2(FriendRequestsFragment())
                },
                goSetting = {
                    goToFriendDepth2(FriendListsFragment())
                },
                goAdd = {
                    goToFriendDepth2(FriendInviteFragment())
                },
                goFriendGoal = { friendInfo ->
                    goToFriendGoal(friendInfo.id,friendInfo.nickname)
                },
                friendList = friendList,
                showBadge = showBadge
            )
        }
    }

    override fun onResume() {
        super.onResume()
        friendViewModel.fetchFriendList()
        friendViewModel.fetchFriendRequest()
    }

    private fun goToFriendDepth2(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .add(R.id.main_container, fragment)
            .addToBackStack(FRIEND_FRAGMENT_STACK)
            .commit()
    }
}

@Composable
private fun FriendHomeView(
    goRequest: () -> Unit,
    goSetting: () -> Unit,
    goAdd: () -> Unit,
    goFriendGoal: (FriendInfo) -> Unit,
    friendList: List<FriendInfo>,
    showBadge: Boolean
) {
    PageDefault {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .padding(top = 20.dp)
                ) {
                    TopHeader(
                        modifier = Modifier.fillMaxWidth()
                            .height(36.dp),
                        title = stringResource(R.string.friend_title),
                        onBackAction = null,
                        textStyle = Typography.Semibold_3XL,
                        otherActionContent = {
                            Row {
                                IconButton(
                                    onClick = goRequest
                                ) {
                                    if (showBadge) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_notification_on),
                                            contentDescription = null,
                                            tint = Color.Unspecified
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_notification_off),
                                            contentDescription = null,
                                            tint = Color.Unspecified
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = goSetting
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_setting),
                                        contentDescription = null,
                                        tint = Color.Unspecified
                                    )
                                }
                            }
                        }
                    )
                    Text(
                        text = stringResource(R.string.friend_sub_title, friendList.size),
                        style = Typography.Medium_XL,
                        color = Black300
                    )
                }
                Spacer(Modifier.height(24.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        friendList,
                        key = FriendInfo::id
                    ) { friendInfo ->
                        FriendItem(
                            friendInfo = friendInfo,
                            onClick = {
                                goFriendGoal(friendInfo)
                            }
                        )
                    }
                    item {
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .align(Alignment.BottomEnd)
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(Blue200)
                    .clickable {
                        goAdd()
                    }
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(R.drawable.ic_person_add),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun FriendItem(
    friendInfo: FriendInfo,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleProfileImageView(
                    modifier = Modifier.size(50.dp),
                    profileImage = friendInfo.profileImage
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = friendInfo.nickname,
                        style = Typography.Semibold_SM
                    )
                    Text(
                        text = stringResource(R.string.friend_goal_count, friendInfo.goalCnt),
                        style = Typography.Medium_SM
                    )
                }
            }
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .dropShadow(
                        shape = RoundedCornerShape(6.dp),
                        shadow = Shadow(
                            radius = 6.dp,
                            spread = 0.5.dp,
                            color = Color.Black.copy(0.25f)
                        )
                    )
                    .background(
                        Color.White,
                        RoundedCornerShape(6.dp)
                    )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(
                        R.string.friend_today_time,
                        friendInfo.todayTime ?: "00:00:00"
                    ),
                    style = Typography.Medium_SM
                )
            }
            Box(
                modifier = Modifier
                    .width(95.dp)
                    .fillMaxHeight()
                    .dropShadow(
                        shape = RoundedCornerShape(6.dp),
                        shadow = Shadow(
                            radius = 6.dp,
                            spread = 0.5.dp,
                            color = Color.Black.copy(0.25f)
                        )
                    )
                    .background(
                        if (friendInfo.isNewPhotoVerify) {
                            Blue100
                        } else {
                            Color.White
                        },
                        RoundedCornerShape(6.dp)
                    )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.friend_photo_verification_btn),
                    style = Typography.Medium_SM.copy(
                        color = Black300
                    )
                )
            }
        }
    }
}

private class FriendInfoPreviewProvider : PreviewParameterProvider<FriendInfo> {
    override val values: Sequence<FriendInfo>
        get() = sequenceOf(
            FriendInfo(
                1,
                "Tester1",
                0,
                "12:34:56",
                false,
                ""
            ), FriendInfo(
                2,
                "Tester2",
                0,
                "12:34:56",
                true,
                ""
            )
        )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun FriendItemPreview(
    @PreviewParameter(FriendInfoPreviewProvider::class) friendInfo: FriendInfo
) {
    Column(
        modifier = Modifier.padding(15.dp)
    ) {
        FriendItem(
            friendInfo = friendInfo,
            onClick = {}
        )
    }
}