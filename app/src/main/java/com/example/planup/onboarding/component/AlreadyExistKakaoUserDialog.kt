package com.example.planup.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpBorderButton
import com.example.planup.component.button.PlanUpButton
import com.example.planup.theme.Black300
import com.example.planup.theme.Black400
import com.example.planup.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlreadyExistKakaoUserDialog(
    email: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        modifier = modifier
            .background(Color.White)
            .clip(RoundedCornerShape(10.dp))
            .padding(horizontal = 20.dp, vertical = 28.dp),
        onDismissRequest = onDismissRequest
    ) {
        AlreadyExistKakaoDialogContent(
            modifier = Modifier,
            email = email,
            isKakaoUser = true,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlreadyExistNormalUserDialog(
    email: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        onDismissRequest = onDismissRequest
    ) {
        AlreadyExistKakaoDialogContent(
            modifier = Modifier,
            email = email,
            isKakaoUser = false,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }
}

@Composable
private fun AlreadyExistKakaoDialogContent(
    email: String,
    isKakaoUser: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.popup_signup_duplicated_email_title),
            style = Typography.Semibold_L.copy(color = Black400),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isKakaoUser) {
                stringResource(R.string.popup_signup_duplicated_kakao_email_content, email)
            } else {
                stringResource(R.string.popup_signup_duplicated_normal_email_content, email)
            },
            style = Typography.Medium_SM.copy(color = Black300)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PlanUpButton(
                modifier = Modifier
                    .weight(1.0f)
                    .height(34.dp),
                title = stringResource(R.string.btn_login),
                onClick = onConfirm
            )

            PlanUpBorderButton(
                modifier = Modifier
                    .weight(1.0f)
                    .height(34.dp),
                title = stringResource(R.string.btn_close),
                onClick = onCancel
            )
        }
    }
}