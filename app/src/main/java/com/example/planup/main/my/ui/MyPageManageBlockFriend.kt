package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.planup.R
import com.example.planup.main.my.data.BlockedFriend
import com.example.planup.main.my.ui.common.RoutePageDefault
import com.example.planup.theme.Blue100
import com.example.planup.theme.Green100
import com.example.planup.theme.Green200
import com.example.planup.theme.Typography

@Composable
fun MyPageManageBlockFriendView(
    onBack: () -> Unit
) {
    MyPageManageBlockFriendContent(
        onBack = onBack
    )
}

@Composable
fun MyPageManageBlockFriendContent(
    onBack: () -> Unit
) {
    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_block)
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun MyPageManageBlockFriendContentPreview() {
    MyPageManageBlockFriendContent(
        onBack = {}
    )
}

@Composable
private fun FriendBlockItem(
    blockFriend: BlockedFriend
) {
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
                onClick = {},
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
                onClick = {},
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
}

@Preview(showBackground = true)
@Composable
fun FriendBlockItemPreview() {
    FriendBlockItem(
        blockFriend = BlockedFriend(1, "test", 0)
    )
}