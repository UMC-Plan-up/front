package com.example.planup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.theme.Blue200
import com.example.planup.theme.SemanticB1
import com.example.planup.theme.SemanticB5
import com.example.planup.theme.Typography

@Composable
fun PlanUpCheckbox(
    text: String,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit) = { },
    style: PlanUpCheckboxStyle = PlanUpCheckboxDefault,
) {
    val interaction = if(style.checkboxSize < 24.dp) {
        Modifier.size(24.dp)
    } else {
        Modifier.size(style.checkboxSize)
    }

    Row(
        modifier = modifier
            .wrapContentSize()
            .clickable(
                enabled = enabled,
                onClick = {
                    onCheckedChange(checked)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = interaction,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(style.checkboxSize),
                painter = if(checked) painterResource(R.drawable.ic_checkbox_checked) else painterResource(R.drawable.ic_checkbox_unchecked),
                contentDescription = null,
                tint = style.checkboxColor
            )
        }

        Spacer(modifier = Modifier.size(style.endPadding))

        Text(
            modifier = Modifier
                .wrapContentSize(),
            text = text,
            style = style.textStyle
        )
    }
}

@Immutable
data class PlanUpCheckboxStyle(
    val checkboxColor: Color,
    val checkboxSize: Dp,
    val endPadding: Dp,
    val textStyle: TextStyle
) {
}

@Stable
val PlanUpCheckboxDefault: PlanUpCheckboxStyle
    get() = PlanUpCheckboxStyle(
        checkboxColor = SemanticB1,
        checkboxSize = 16.dp,
        endPadding = 2.dp,
        textStyle = Typography.Regular_S
    )

@Preview
@Composable
private fun PlanUpCheckboxPreview() {
    PlanUpCheckbox(
        modifier = Modifier
            .wrapContentSize()
            .background(SemanticB5)
            .padding(8.dp),
        text = "테스트 메세지",
        checked = true
    )
}

@Preview
@Composable
private fun PlanUpSmallCheckboxPreview() {
    PlanUpCheckbox(
        modifier = Modifier
            .wrapContentSize()
            .background(SemanticB5)
            .padding(8.dp),
        text = "테스트 메세지",
        checked = true,
        style = PlanUpCheckboxDefault.copy(checkboxSize = 12.dp)
    )
}

@Preview
@Composable
private fun PlanUpBlueCheckboxPreview() {
    PlanUpCheckbox(
        modifier = Modifier
            .wrapContentSize()
            .background(SemanticB5)
            .padding(8.dp),
        text = "테스트 메세지",
        checked = true,
        style = PlanUpCheckboxDefault.copy(checkboxColor = Blue200)
    )
}