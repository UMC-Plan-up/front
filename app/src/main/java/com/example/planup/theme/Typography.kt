package com.example.planup.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.planup.R

val pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
)

object Typography {

    private val defaultStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.015.sp,
        lineHeight = 1.5.em
    )

    // 함수로 텍스트 스타일 생성
    private fun style(fontSize: TextUnit, fontWeight: FontWeight = FontWeight.Medium) = defaultStyle.copy(
        fontSize = fontSize,
        fontWeight = fontWeight
    )

    // Semibold
    val Semibold_3XL = style(24.sp, FontWeight.SemiBold)
    val Semibold_2XL = style(22.sp, FontWeight.SemiBold)
    val Semibold_XL  = style(20.sp, FontWeight.SemiBold)
    val Semibold_L   = style(28.sp, FontWeight.SemiBold)
    val Semibold_SM  = style(16.sp, FontWeight.SemiBold)
    val Semibold_S   = style(14.sp, FontWeight.SemiBold)

    // Medium
    val Medium_2XL = style(22.sp)
    val Medium_XL  = style(20.sp)
    val Medium_L   = style(18.sp)
    val Medium_SM  = style(16.sp)
    val Medium_S   = style(14.sp)
    val Medium_XS  = style(12.sp)
    val Medium_2XS = style(11.sp)
}