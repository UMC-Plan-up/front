package com.example.planup.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.theme.SemanticB4
import com.example.planup.theme.Typography

@Composable
fun OnBoardingSecureTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = Typography.Medium_S,
    useVisibility: Boolean = true
) {
    var isVisible by remember { mutableStateOf(false) }

    BasicSecureTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        state = state,
        decorator = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .border(
                        width = 1.dp,
                        color = SemanticB4,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1.0f),
                ) {
                    innerTextField()
                }

                if(useVisibility) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                enabled = true,
                                onClick = {
                                    isVisible = !isVisible
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .wrapContentWidth(),
                            imageVector = if(isVisible) ImageVector.vectorResource(R.drawable.ic_eye_on) else ImageVector.vectorResource(R.drawable.ic_eye_off),
                            contentDescription = null,
                            tint = Color(0xFF4D4D4D)
                        )
                    }
                }
            }
        },
        enabled = enabled,
        textStyle = textStyle,
        textObfuscationMode = if(isVisible) TextObfuscationMode.Visible else TextObfuscationMode.Hidden
    )
}

@Preview
@Composable
private fun OnBoardingSecureTextFieldPreview() {
    Box(
        modifier = Modifier
            .size(height = 100.dp, width = 200.dp)
            .background(color = Color.Gray)
    ) {
        OnBoardingSecureTextField(
            state = rememberTextFieldState(initialText = "테스트 텍스트"),
            modifier = Modifier,
            useVisibility = false
        )
    }
}

@Preview
@Composable
private fun OnBoardingSecureTextFieldIconPreview() {
    Box(
        modifier = Modifier
            .size(height = 100.dp, width = 200.dp)
            .background(color = Color.Gray)
    ) {
        OnBoardingSecureTextField(
            state = rememberTextFieldState(initialText = "테스트 텍스트"),
            modifier = Modifier
        )
    }
}