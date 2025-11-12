package com.example.planup.component

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
import com.example.planup.theme.Black100
import com.example.planup.theme.Black300
import com.example.planup.theme.pretendard

@Composable
fun PlanUpButtonSecondarySmall(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = true,
        modifier = Modifier
            .height(36.dp)
            .then(modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = Black100,
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = title,
            color = Black300,
            fontFamily = pretendard,
        )
    }
}


@Preview
@Composable
private fun PlanUpButtonSecondarySmallPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PlanUpButtonSecondarySmall(
            modifier = Modifier.width(320.dp),
            title = "다음",
            onClick = {}
        )
    }
}
