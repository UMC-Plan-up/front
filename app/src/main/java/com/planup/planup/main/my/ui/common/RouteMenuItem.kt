package com.planup.planup.main.my.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.planup.planup.R
import com.planup.planup.theme.SemanticB5
import com.planup.planup.theme.Typography

@Composable
fun RouteMenuItem(
    title: String,
    rightContent : (@Composable RowScope.() -> Unit)? = null,
    action: (() -> Unit)? = null
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable(action != null) {
                    action?.invoke()
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = Typography.Semibold_S
            )
            rightContent?.let {content ->
                Row {
                    content()
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = SemanticB5
        )
    }
}


@Composable
fun RouteMenuItemWithArrow(
    title: String,
    action: () -> Unit
) {
    RouteMenuItem(
        title = title,
        rightContent = {
            Box(
                modifier = Modifier.size(24.dp)
            ) {
                Image(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                )
            }
        },
        action = action
    )
}


@Composable
@Preview
private fun RouteMenuItemPreview() {
    Column {
        RouteMenuItem("test") { }
        RouteMenuItemWithArrow("test2") { }
    }
}