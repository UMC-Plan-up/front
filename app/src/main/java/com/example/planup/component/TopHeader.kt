package com.example.planup.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.theme.Black400
import com.example.planup.theme.Typography

@Composable
fun TopHeader(
    modifier: Modifier = Modifier,
    onBackAction: (() -> Unit)? = null,
    otherActionContent: @Composable RowScope.() -> Unit = {},
    title: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            onBackAction?.let { onBack ->
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        null
                    )
                }
            }
            Text(
                text = title,
                style = Typography.Medium_2XL,
                color = Black400
            )
        }
        otherActionContent()
    }
}