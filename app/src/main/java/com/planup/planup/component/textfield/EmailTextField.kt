package com.planup.planup.component.textfield

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.planup.planup.R
import com.planup.planup.theme.Black200
import com.planup.planup.theme.Typography

@Composable
fun EmailTextField(
    state: TextFieldState,
) {
    val showPlaceholderText by remember {
        derivedStateOf {
            state.text.isEmpty()
        }
    }
    var showDropDown by remember { mutableStateOf(false) }
    Box() {
        BasicTextField(
            state = state,
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
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (showPlaceholderText) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterStart),
                                text = stringResource(R.string.email_enter),
                                style = Typography.Medium_SM,
                                color = Black200
                            )
                        }
                        innerTextField()
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = {
                                showDropDown = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_down),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            },
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            )
        )
        DropdownMenu(
            modifier = Modifier.align(Alignment.BottomEnd),
            expanded = showDropDown,
            onDismissRequest = {
                showDropDown = false
            }
        ) {
            listOf(
                stringResource(R.string.dropdown_gmail),
                stringResource(R.string.dropdown_naver),
                stringResource(R.string.dropdown_kakao),
            ).forEach { emailSuffix ->
                DropdownMenuItem(
                    text = {
                        Text(emailSuffix)
                    },
                    onClick = {
                        state.edit {
                            val emailText = this.originalText.toString()
                            if (!emailText.contains("@")) {
                                this.append(emailSuffix)
                            } else {
                                val index = emailText.indexOf("@")
                                this.replace(index, emailText.length, emailSuffix)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun EmailTextFieldPreview() {
    val state = remember { TextFieldState() }
    Column(
        modifier = Modifier.safeDrawingPadding()
    ) {
        EmailTextField(
            state = state
        )
    }
}
