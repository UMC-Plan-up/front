package com.planup.planup.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.planup.planup.theme.Blue100
import com.planup.planup.theme.Green100
import com.planup.planup.theme.Green300
import com.planup.planup.theme.Red100
import com.planup.planup.theme.Red200
import com.planup.planup.theme.Typography

sealed interface SmallButtonType {
    data object Red : SmallButtonType
    data object Green : SmallButtonType
    data object Blue : SmallButtonType
}

@Composable
private fun SmallButtonType.getButtonColors(): ButtonColors {
    return when (this) {
        SmallButtonType.Blue -> ButtonDefaults.buttonColors(
            containerColor = Blue100,
            contentColor = Color(0xff203358)
        )

        SmallButtonType.Green -> ButtonDefaults.buttonColors(
            containerColor = Green100,
            contentColor = Green300
        )

        SmallButtonType.Red -> ButtonDefaults.buttonColors(
            containerColor = Red100,
            contentColor = Red200
        )
    }
}

@Composable
fun PlanUpSmallButton(
    smallButtonType: SmallButtonType,
    onClick: () -> Unit,
    title: String
) {
    Button(
        modifier = Modifier.height(30.dp),
        onClick = onClick,
        colors = smallButtonType.getButtonColors(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(
            horizontal = 5.dp
        )
    ) {
        Text(
            text = title,
            style = Typography.Medium_SM,
        )
    }
}

private class PlanUpSmallGreenButtonProvider : PreviewParameterProvider<SmallButtonType> {
    override val values: Sequence<SmallButtonType>
        get() = sequenceOf(
            SmallButtonType.Green,
            SmallButtonType.Blue,
            SmallButtonType.Red
        )
}

@Composable
@Preview
private fun PlanUpSmallButtonPreview(
    @PreviewParameter(PlanUpSmallGreenButtonProvider::class) type : SmallButtonType
) {
    PlanUpSmallButton(
        smallButtonType = type,
        onClick = {},
        title = "test"
    )
}