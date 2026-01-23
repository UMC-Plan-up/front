package com.example.planup.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.theme.Black300
import com.example.planup.theme.Black400
import com.example.planup.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptFriendRequestDialog(
    nickname: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.friend_new),
                    style = Typography.Medium_L.copy(color = Black400),
                    textAlign = TextAlign.Start
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.friend_description, nickname),
                    style = Typography.Medium_SM.copy(color = Black300),
                    textAlign = TextAlign.Start
                )
            }

            Icon(
                modifier = Modifier
                    .size(118.dp),
                painter = painterResource(R.drawable.ic_gift),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                PlanUpButton(
                    title = stringResource(R.string.btn_ok),
                    onClick = onConfirm
                )
            }
        }
    }
}


@Preview
@Composable
private fun AcceptFriendRequestDialogPreview() {
    AcceptFriendRequestDialog(
        modifier = Modifier
            .fillMaxSize(),
        nickname = "사용자",
        onDismissRequest = {},
        onConfirm = {}
    )
}