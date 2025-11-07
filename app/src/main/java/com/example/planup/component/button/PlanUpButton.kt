package com.example.planup.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.theme.Black200
import com.example.planup.theme.Blue200
import com.example.planup.theme.Typography
import com.example.planup.theme.fontColor

@Composable
fun PlanUpButton(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .height(44.dp)
            .then(modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = Blue200,
            disabledContainerColor = Black200
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = title,
            color = fontColor,
            style = Typography.Medium_SM
        )
    }
}


@Preview
@Composable
private fun PlanUpButtonPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PlanUpButton(
            modifier = Modifier.width(320.dp),
            title = "다음",
            onClick = {}
        )
        PlanUpButton(
            modifier = Modifier.width(320.dp),
            title = "다음",
            enabled = false,
            onClick = {}
        )
    }
}
