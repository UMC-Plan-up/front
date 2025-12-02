package com.example.planup.main.my.ui

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.my.ui.common.MyPageDefault
import com.example.planup.main.my.ui.viewmodel.EmailChangeUiMessage
import com.example.planup.main.my.ui.viewmodel.MyPageEmailChangeViewModel
import com.example.planup.theme.Black200
import com.example.planup.theme.Black300
import com.example.planup.theme.Blue300
import com.example.planup.theme.Typography

@Composable
fun MyPageChangeEmailView(
    onBack: () -> Unit = {},
    emailChangeViewModel: MyPageEmailChangeViewModel,
    mainSnackbarViewModel: MainSnackbarViewModel

) {
    var showStep2 by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        emailChangeViewModel.emailChangeUiMessage.collect { uiMessage ->
            when (uiMessage) {
                is EmailChangeUiMessage.Error -> {
                    mainSnackbarViewModel.updateErrorMessage(uiMessage.message)
                }

                is EmailChangeUiMessage.EmailSendSuccess -> {
                    showStep2 = true
                }
            }
        }
    }
    MyPageChangeEmailContent(
        onBack = onBack,
        sendEmail = emailChangeViewModel::sendEmail,
        showStep2 = showStep2
    )
}

@Composable
fun MyPageChangeEmailContent(
    onBack: () -> Unit = {},
    sendEmail: (email: String) -> Unit,
    showStep2: Boolean,
) {
    val email = remember {
        mutableStateOf(TextFieldValue(""))
    }

    MyPageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_email)
    ) {
        Spacer(Modifier.height(40.dp))
        AnimatedVisibility(!showStep2) {
            ChangeEmailStep1(
                email = email,
                sendEmail = {
                    sendEmail(email.value.text)
                }
            )
        }
        AnimatedVisibility(showStep2) {
            ChangeEmailStep2(
                email.value.text
            )
        }
    }
}

@Composable
private fun ChangeEmailStep1(
    email: MutableState<TextFieldValue>,
    sendEmail: () -> Unit
) {
    val enableButton by remember(email) {
        derivedStateOf {
            Patterns.EMAIL_ADDRESS.matcher(email.value.text).matches()
        }
    }

    val showPlaceholder by remember(email) {
        derivedStateOf {
            email.value.text.isEmpty()
        }
    }

    var showDropDownMenu by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_email),
                    style = Typography.Medium_L
                )
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

                    BasicTextField(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size
                            },
                        value = email.value,
                        onValueChange = {
                            email.value = it
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        textStyle = Typography.Medium_SM,
                        decorationBox = { innerTextField ->
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .border(1.dp, Blue300, RoundedCornerShape(6.dp))
                                    .padding(
                                        horizontal = 6.dp,
                                        vertical = 2.dp
                                    ),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                innerTextField()
                                if (showPlaceholder) {
                                    Text(
                                        text = stringResource(R.string.email_enter),
                                        style = Typography.Medium_SM,
                                        color = Black300
                                    )
                                }
                                IconButton(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    onClick = {
                                        showDropDownMenu = !showDropDownMenu
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_arrow_down_email),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    )

                    if (showDropDownMenu) {
                        Popup(
                            offset = with(LocalDensity.current) {
                                IntOffset(
                                    x = textFieldSize.width - 200, // dropdown width 만큼 조정 가능
                                    y = textFieldSize.height
                                )
                            },
                            onDismissRequest = {
                                showDropDownMenu = false
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .border(1.dp, Black200, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .wrapContentWidth()
                            ) {
                                listOf(
                                    stringResource(R.string.dropdown_gmail),
                                    stringResource(R.string.dropdown_naver),
                                    stringResource(R.string.dropdown_kakao),
                                ).forEach { emailSuffix ->
                                    TextButton(
                                        onClick = {
                                            val emailText = email.value.text
                                            val newText = if (!emailText.contains("@")) {
                                                emailText + emailSuffix
                                            } else {
                                                emailText.substringBefore("@") + emailSuffix
                                            }
                                            email.value = email.value.copy(
                                                text = newText,
                                                selection = TextRange(newText.length)
                                            )
                                        }
                                    ) {
                                        Text(
                                            text = emailSuffix,
                                            style = Typography.Medium_S,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        PlanUpButton(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.btn_get_link),
            enabled = enableButton,
            onClick = sendEmail
        )
    }
}

@Composable
private fun ChangeEmailStep2(
    emailString: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                R.string.link_by_email,
                emailString
            ),
            style = Typography.Medium_SM,
            textAlign = TextAlign.Center
        )
        Spacer(
            modifier = Modifier.height(72.dp)
        )
        Text(
            modifier = Modifier.clickable {

            },
            text = stringResource(
                R.string.not_receive_email
            ),
            style = Typography.Medium_L,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyPageChangeEmailContentPreview() {
    MyPageChangeEmailContent(
        onBack = {},
        sendEmail = {},
        showStep2 = false
    )
}