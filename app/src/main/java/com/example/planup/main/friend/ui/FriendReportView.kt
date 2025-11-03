package com.example.planup.main.friend.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.PlanUpRedButton
import com.example.planup.component.PlanUpSwitch
import com.example.planup.main.my.ui.common.RouteMenuItem
import com.example.planup.theme.Black100
import com.example.planup.theme.Typography

@Composable
fun FriendReportView(
    report: (reason: String, withBlock: Boolean) -> Unit
) {
    FriendReportContent(
        report = report
    )
}

@Composable
fun FriendReportContent(
    report: (reason: String, withBlock: Boolean) -> Unit
) {
    var reason by remember {
        mutableStateOf("")
    }
    var withBlock by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp
            )
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .height(90.dp)
                .padding(12.dp)
                .padding(bottom = 2.dp)
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "신고",
                style = Typography.Semibold_2XL
            )
            Text(
                text = "사용자 신고 사유를 알려주세요.",
                style = Typography.Medium_L
            )
        }
        Column {
            listOf(
                "욕설/비방/혐오 표현 사용",
                "음란물/선정적 내용",
                "스팸/광고",
                "사기/허위 정보",
                "개인정보 노출/유출",
                "불쾌하거나 부적절한 내용",
                "기타"
            ).forEach { title ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(37.dp)
                        .clickable(true) {
                            reason = title
                        }
                        .drawBehind {
                            if (reason == title) {
                                drawRect(Black100)
                            }
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = Typography.Medium_SM
                    )
                }
                HorizontalDivider(
                    color = Black100
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 2.dp),
                text = "해당 유저를 차단하시겠어요?",
                style = Typography.Medium_L
            )
            RouteMenuItem(
                title = "해당 유저 차단하기",
                rightContent = {
                    PlanUpSwitch(
                        checked = withBlock,
                        onCheckedChange = {
                            withBlock = it
                        }
                    )
                }
            )
        }

        PlanUpRedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = reason.isNotEmpty(),
            title = stringResource(R.string.btn_report_complete),
            onClick = {
                report(reason, withBlock)
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun FriendReportContentPreview() {
    FriendReportContent(
        report = { _, _ -> }
    )
}