package com.example.planup.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpBorderButton
import com.example.planup.component.button.PlanUpButton
import com.example.planup.theme.Typography

@Composable
private fun PlanUpAlertBase(
    headerText: String? = null,
    title: String,
    subTitle: String? = null,
    buttonRows: @Composable () -> Unit
) {
    OutlinedCard(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xffdadada)),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            headerText?.let { header ->
                Text(
                    modifier = Modifier,
                    text = header,
                    style = Typography.Semibold_L
                )
            }
            Column(
                modifier = Modifier
                    .heightIn(96.dp),
                verticalArrangement = if (subTitle == null) {
                    Arrangement.Center
                } else {
                    Arrangement.spacedBy(12.dp)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier,
                    text = title,
                    style = Typography.Medium_SM
                )
                subTitle?.let { sub ->
                    Text(
                        modifier = Modifier,
                        text = sub,
                        style = Typography.Medium_S,
                        textAlign = TextAlign.Center
                    )
                }
            }
            buttonRows()
        }
    }
}


@Composable
fun PlanUpAlertBaseContent(
    leftButtonTitle: String = stringResource(R.string.btn_cancel),
    rightButtonTitle: String = stringResource(R.string.btn_ok),
    headerText: String? = null,
    title: String,
    subTitle: String? = null,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    PlanUpAlertBase(
        headerText = headerText,
        title = title,
        subTitle = subTitle,
        buttonRows = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PlanUpBorderButton(
                    modifier = Modifier.weight(1f),
                    title = leftButtonTitle,
                    onClick = onDismissRequest
                )
                PlanUpButton(
                    modifier = Modifier.weight(1f),
                    title = rightButtonTitle,
                    onClick = onConfirm
                )
            }
        }
    )
}

@Composable
fun PlanUpAlertBaseSingleContent(
    buttonTitle: String = stringResource(R.string.btn_ok),
    headerText : String? = null,
    title: String,
    subTitle: String? = null,
    onConfirm: () -> Unit
) {
    PlanUpAlertBase(
        headerText = headerText,
        title = title,
        subTitle = subTitle,
        buttonRows = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                PlanUpButton(
                    modifier = Modifier.width(92.dp),
                    title = buttonTitle,
                    onClick = onConfirm
                )
            }
        }
    )
}


@Preview
@Composable
private fun PlanUpAlertBaseContentPreview() {
    PlanUpAlertBaseContent(
        title = "로그아웃 하시겠어요?",
        onDismissRequest = {},
        onConfirm = {}
    )
}

@Preview
@Composable
private fun PlanUpAlertBaseSingleContentPreview() {
    PlanUpAlertBaseSingleContent(
        title = "로그아웃 하시겠어요?",
        onConfirm = {}
    )
}