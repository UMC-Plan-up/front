package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.R
import com.example.planup.component.ValidateRow
import com.example.planup.component.button.PlanUpButton
import com.example.planup.component.textfield.EmailTextField
import com.example.planup.component.textfield.PasswordTextField
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.my.ui.common.MyPageDefault
import com.example.planup.main.my.ui.viewmodel.MyPagePasswordChangeViewModel
import com.example.planup.main.my.ui.viewmodel.MyPagePasswordEvent
import com.example.planup.theme.Typography

@Composable
fun MyPagePasswordChangeView(
    onBack: () -> Unit,
    myPagePasswordChangeViewModel: MyPagePasswordChangeViewModel = hiltViewModel(),
    mainSnackbarViewModel: MainSnackbarViewModel
) {
    var showStep2 by rememberSaveable() {
        mutableStateOf(false)
    }
    LaunchedEffect(myPagePasswordChangeViewModel) {
        myPagePasswordChangeViewModel.uiEvent.collect { event ->
            when (event) {
                MyPagePasswordEvent.SuccessLogin -> {
                    showStep2 = true
                }

                is MyPagePasswordEvent.Error -> {
                    mainSnackbarViewModel.updateErrorMessage(event.message)
                }
            }
        }
    }
    MyPageDefault(
        onBack = onBack,
        categoryText = if (showStep2) {
            stringResource(R.string.mypage_password)
        } else {
            stringResource(R.string.mypage_password_check)
        }
    ) {
        if (showStep2) {
            MyPagePasswordChangeContent(
                newPasswordInput = myPagePasswordChangeViewModel.newPasswordInput,
                newPasswordReInput = myPagePasswordChangeViewModel.newPasswordReInput,
                changePassword = {}
            )
        } else {
            MyPagePasswordCheckContent(
                emailInput = myPagePasswordChangeViewModel.emailInput,
                passwordInput = myPagePasswordChangeViewModel.passwordInput,
                loginCheck = myPagePasswordChangeViewModel::loginCheck
            )
        }
    }
}

@Composable
private fun MyPagePasswordCheckContent(
    emailInput: TextFieldState,
    passwordInput: TextFieldState,
    loginCheck: () -> Unit = {}
) {
    val enabledButton by remember {
        derivedStateOf {
            emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column() {
            Text(
                text = stringResource(R.string.mypage_password_check_sub),
                style = Typography.Medium_S
            )
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "이메일",
                        style = Typography.Medium_L
                    )
                    EmailTextField(
                        state = emailInput
                    )

                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "비밀번호",
                        style = Typography.Medium_L
                    )
                    PasswordTextField(
                        state = passwordInput
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            PlanUpButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.btn_ok),
                enabled = enabledButton,
                onClick = loginCheck
            )
        }
    }
}

@Composable
private fun MyPagePasswordChangeContent(
    newPasswordInput: TextFieldState,
    newPasswordReInput: TextFieldState,
    changePassword : () -> Unit
) {

    var isCorrectLength by remember {
        mutableStateOf(false)
    }
    var isCorrectDigit by remember {
        mutableStateOf(false)
    }
    var isCorrectSpecial by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(newPasswordInput) {
        isCorrectLength = newPasswordInput.text.length in 8..20
        isCorrectDigit = newPasswordInput.text.any { it.isDigit() }
        isCorrectSpecial = newPasswordInput.text.any { !it.isLetterOrDigit() }
    }
    val enabledButton by remember {
        derivedStateOf {
            isCorrectLength && isCorrectDigit && isCorrectSpecial && newPasswordInput == newPasswordReInput
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column() {
            Text(
                text = stringResource(R.string.mypage_password_check_sub),
                style = Typography.Medium_S
            )
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.new_password),
                            style = Typography.Medium_L
                        )
                        Text(
                            text = stringResource(R.string.password_condition),
                            style = Typography.Medium_S
                        )
                    }
                    PasswordTextField(
                        state = newPasswordInput
                    )
                    Row() {
                        ValidateRow(
                            validateText = "8-20자 이내",
                            isValidate = isCorrectLength
                        )
                        ValidateRow(
                            validateText = "숫자, 특수문자 포함",
                            isValidate = isCorrectDigit && isCorrectSpecial
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.recheck_password),
                        style = Typography.Medium_L
                    )
                    PasswordTextField(
                        state = newPasswordReInput,
                        placeholderText = stringResource(R.string.password_again)
                    )
                    Row() {
                        ValidateRow(
                            validateText = "비밀번호 일치",
                            isValidate = newPasswordInput.text == newPasswordReInput.text
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            PlanUpButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.btn_ok),
                enabled = enabledButton,
                onClick = changePassword
            )
        }
    }
}

@Preview
@Composable
fun MyPagePasswordCheckContentPreview() {
    val state1 = remember { TextFieldState() }
    val state2 = remember { TextFieldState() }

    MyPagePasswordCheckContent(
        emailInput = state1,
        passwordInput = state2,
        loginCheck = {}
    )
}