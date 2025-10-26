package com.example.planup.main.friend.ui.componet

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
import com.example.planup.R
import com.example.planup.component.PlanUpButtonSecondarySmall
import com.example.planup.component.PlanUpButtonSmall
import com.example.planup.main.friend.data.FriendRequestsResult

@Composable
fun FriendRequestItem(
    friendInfo: FriendRequestsResult,
    clickItem: () -> Unit,
    acceptFriend: () -> Unit,
    declineFriend: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.clickable {

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
        FriendRequestsResult(
            id = 1,
            nickname = "Tester",
            goalCnt = 3,
            todayTime = "00:01:54",
            isNewPhotoVerify = true,
            profileImage = ""
        ),
        {},
        {},
        {}
    )
}