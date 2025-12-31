package com.example.planup.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.theme.SemanticB4
import com.example.planup.theme.Typography

@Composable
fun OnBoardingTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = Typography.Medium_S,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputTransformation: InputTransformation? = null
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        state = state,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
        inputTransformation = inputTransformation,
        lineLimits = TextFieldLineLimits.SingleLine,
        decorator = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .border(
                        width = 1.dp,
                        color = SemanticB4,
                        shape = RoundedCornerShape(4.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(horizontal = 8.dp),
                ) {
                    innerTextField()
                }
            }
        }
    )
}

@Preview
@Composable
private fun OnBoardingTextFieldPreview() {
    Box(
        modifier = Modifier
            .size(height = 100.dp, width = 200.dp)
            .background(color = Color.Gray)
    ) {
        OnBoardingTextField(
            state = rememberTextFieldState(initialText = "테스트 텍스트"),
            modifier = Modifier
        )
    }
}