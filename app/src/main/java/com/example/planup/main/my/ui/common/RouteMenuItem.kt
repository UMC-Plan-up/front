package com.example.planup.main.my.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.theme.SemanticB5

@Composable
fun RouteMenuItem(
    title: String,
    showArrow: Boolean = false,
    action: () -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable {
                    action()
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title
            )
            if (showArrow) {
                Image(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = SemanticB5
        )
    }
}

@Composable
@Preview
private fun RouteMenuItemPreview() {
    Column {
        RouteMenuItem("test") { }
        RouteMenuItem("test2",true) { }
    }
}