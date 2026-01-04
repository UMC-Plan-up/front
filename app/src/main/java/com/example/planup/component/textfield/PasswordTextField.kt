package com.example.planup.component.textfield

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.theme.Black200
import com.example.planup.theme.Typography

@Composable
fun PasswordTextField(
    state: TextFieldState,
    placeholderText: String = stringResource(R.string.password_enter)
) {
    val showPlaceholderText by remember {
        derivedStateOf {
            state.text.isEmpty()
        }
    }

    var passwordMode by remember {
        mutableStateOf(TextObfuscationMode.RevealLastTyped)
    }

    BasicSecureTextField(
        state = state,
        textObfuscationMode = passwordMode,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Black200, RoundedCornerShape(4.dp))
            .height(44.dp)
            .padding(6.dp),
        textStyle = Typography.Medium_SM,
        decorator = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp, end = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (showPlaceholderText) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterStart),
                            text = placeholderText,
                            style = Typography.Medium_SM,
                            color = Black200
                        )
                    }
                    innerTextField()
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            if (passwordMode == TextObfuscationMode.RevealLastTyped) {
                                passwordMode = TextObfuscationMode.Visible
                            } else {
                                passwordMode = TextObfuscationMode.RevealLastTyped
                            }
                        }
                    ) {
                        Icon(
                            painter = if (passwordMode == TextObfuscationMode.RevealLastTyped) {
                                painterResource(R.drawable.ic_eye_off)
                            } else {
                                painterResource(R.drawable.ic_eye_on)
                            },
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun PasswordTextFieldPreview() {
    val state = remember { TextFieldState() }
    PasswordTextField(
        state = state
    )
}
