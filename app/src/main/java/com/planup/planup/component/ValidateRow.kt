package com.planup.planup.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.planup.planup.R
import com.planup.planup.theme.Black200
import com.planup.planup.theme.Green200
import com.planup.planup.theme.Typography

@Composable
fun ValidateRow(
    validateText: String,
    isValidate: Boolean
) {
    val validateColor = if (isValidate) Green200 else Black200
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check_validate),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = validateColor
        )
        Text(
            text = validateText,
            color = validateColor,
            style = Typography.Medium_S
        )
    }
}

@Preview
@Composable
private fun ValidateRowPreview() {
    ValidateRow(
        validateText = "8-20자 이내",
        isValidate = true
    )
}