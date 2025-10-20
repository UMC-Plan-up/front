package com.example.planup.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.planup.R
import com.example.planup.theme.Typography.defaultTextStyle

val pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
)

object Typography {

    private val defaultTextStyle = TextStyle(
        fontFamily = pretendard,
        lineHeight = (1.5).em,
        letterSpacing = (0.15).sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None
        )
    )

    val medium_SM
        get() = defaultTextStyle.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

    val medium_XL
        get() = defaultTextStyle.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

    val semiBold_3XL
        get() = defaultTextStyle.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
}