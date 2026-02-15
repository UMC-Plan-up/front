package com.planup.planup.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.planup.planup.theme.Black200
import com.planup.planup.theme.Typography

@Composable
fun PlanUpBorderButton(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .height(44.dp)
            .then(modifier),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            disabledContainerColor = Black200,
        ),
        border = BorderStroke(2.dp,Black200),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = title,
            style = Typography.Medium_SM,
        )
    }
}


@Preview
@Composable
private fun PlanUpBorderButtonPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PlanUpBorderButton(
            modifier = Modifier.width(320.dp),
            title = "다음",
            onClick = {}
        )
        PlanUpBorderButton(
            modifier = Modifier.width(320.dp),
            title = "다음",
            enabled = false,
            onClick = {}
        )
    }
}
