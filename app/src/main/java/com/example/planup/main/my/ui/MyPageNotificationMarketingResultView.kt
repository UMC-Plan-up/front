package com.example.planup.main.my.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.PlanUpButton
import com.example.planup.theme.Black300
import com.example.planup.theme.Typography

@Composable
fun MyPageNotificationMarketingResultView(
    onBack: () -> Unit,
    isAgree: Boolean,
    nickName: String,
    date: String
) {
    Column(
        modifier = Modifier
            .size(300.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            if (isAgree) {
                Text(
                    text = stringResource(R.string.popup_benefit_title_agree),
                    style = Typography.Semibold_L
                )
            } else {
                Text(
                    text = stringResource(R.string.popup_benefit_title_disagree),
                    style = Typography.Semibold_L
                )
            }

            if (isAgree) {
                Text(
                    text = stringResource(
                        R.string.popup_benefit_explain_agree,
                        nickName,
                        date
                    ),
                    style = Typography.Medium_S,
                    textAlign = TextAlign.Center,
                    color = Black300
                )
            } else {
                Text(
                    text = stringResource(
                        R.string.popup_benefit_explain_disagree,
                        nickName,
                        date
                    ),
                    style = Typography.Medium_S,
                    textAlign = TextAlign.Center,
                    color = Black300
                )
            }

            PlanUpButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.btn_ok),
                onClick = onBack
            )
        }
    }
}

@Composable
@Preview(showBackground = false)
private fun MyPageNotificationAgreePreview() {
    MyPageNotificationMarketingResultView(
        onBack = {},
        isAgree = true,
        nickName = "닉네임",
        date = "2025년 7월 12일"
    )
}