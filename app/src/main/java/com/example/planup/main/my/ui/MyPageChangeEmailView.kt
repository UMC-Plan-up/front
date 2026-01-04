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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.component.textfield.EmailTextField
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.my.ui.common.MyPageDefault
import com.example.planup.main.my.ui.viewmodel.EmailChangeUiMessage
import com.example.planup.main.my.ui.viewmodel.MyPageEmailChangeViewModel
import com.example.planup.theme.Black200
import com.example.planup.theme.Black300
import com.example.planup.theme.Blue300
import com.example.planup.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageChangeEmailView(
    onBack: () -> Unit = {},
    emailChangeViewModel: MyPageEmailChangeViewModel,
    mainSnackbarViewModel: MainSnackbarViewModel

) {
    var showStep2 by rememberSaveable {
        mutableStateOf(false)
    }
    var showLoading by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        emailChangeViewModel.emailChangeUiMessage.collect { uiMessage ->
            when (uiMessage) {
                is EmailChangeUiMessage.Error -> {
                    mainSnackbarViewModel.updateErrorMessage(uiMessage.message)
                    showLoading = false
                }

                is EmailChangeUiMessage.EmailSendSuccess -> {
                    showStep2 = true
                    showLoading = false
                }

                is EmailChangeUiMessage.EmailReSendSuccess -> {
                    showLoading = false
                }

                EmailChangeUiMessage.Loading -> {
                    showLoading = true
                }
            }
        }
    }

    MyPageChangeEmailContent(
        onBack = onBack,
        sendEmail = emailChangeViewModel::sendEmail,
        resendEmail = emailChangeViewModel::reSendEmail,
        showStep2 = showStep2
    )
    if (showLoading) {
        BasicAlertDialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun MyPageChangeEmailContent(
    onBack: () -> Unit = {},
    sendEmail: (email: String) -> Unit,
    resendEmail: (email : String) -> Unit,
    showStep2: Boolean,
) {
    val email = rememberTextFieldState()

    MyPageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_email)
    ) {
        Spacer(Modifier.height(40.dp))
        AnimatedVisibility(!showStep2) {
            ChangeEmailStep1(
                email = email,
                sendEmail = {
                    sendEmail(email.text.toString())
                }
            )
        }
        AnimatedVisibility(showStep2) {
            ChangeEmailStep2(
                emailString = email.text.toString(),
                resendEmail = {
                    resendEmail(email.text.toString())
                }
            )
        }
    }
}

@Composable
private fun ChangeEmailStep1(
    email: TextFieldState,
    sendEmail: () -> Unit
) {
    val enableButton by remember(email) {
        derivedStateOf {
            Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
        }
    }

    val showPlaceholder by remember(email) {
        derivedStateOf {
            email.text.isEmpty()
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
                EmailTextField(
                    state = email
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeEmailStep2(
    emailString: String,
    resendEmail :() -> Unit
) {
    var showReSendSheet by remember {
        mutableStateOf(false)
    }
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
                showReSendSheet = true
            },
            text = stringResource(
                R.string.not_receive_email
            ),
            style = Typography.Medium_L,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )
    }
    if (showReSendSheet) {
        MyPageChangeEmailSheet(
            onDismissRequest = {
                showReSendSheet = false
            },
            resendEmail = {

                showReSendSheet = false
            },
            useKaKaoLogin = {

                showReSendSheet = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyPageChangeEmailContentPreview() {
    MyPageChangeEmailContent(
        onBack = {},
        sendEmail = {},
        resendEmail = {},
        showStep2 = false
    )
}