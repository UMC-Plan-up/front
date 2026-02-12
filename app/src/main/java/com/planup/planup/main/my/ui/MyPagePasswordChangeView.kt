package com.planup.planup.main.my.ui

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.planup.planup.R
import com.planup.planup.component.PlanUpAlertBaseContent
import com.planup.planup.component.PlanUpAlertBaseSingleContent
import com.planup.planup.component.ValidateRow
import com.planup.planup.component.button.PlanUpButton
import com.planup.planup.component.textfield.EmailTextField
import com.planup.planup.component.textfield.PasswordTextField
import com.planup.planup.main.MainSnackbarViewModel
import com.planup.planup.main.my.ui.common.MyPageDefault
import com.planup.planup.main.my.ui.viewmodel.MyPagePasswordChangeViewModel
import com.planup.planup.main.my.ui.viewmodel.MyPagePasswordEvent
import com.planup.planup.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPagePasswordChangeView(
    onBack: () -> Unit,
    myPagePasswordChangeViewModel: MyPagePasswordChangeViewModel = hiltViewModel(),
    mainSnackbarViewModel: MainSnackbarViewModel
) {
    var showStep2 by rememberSaveable() {
        mutableStateOf(false)
    }
    var showChangeFinishAlert by rememberSaveable() {
        mutableStateOf(false)
    }
    LaunchedEffect(myPagePasswordChangeViewModel) {
        myPagePasswordChangeViewModel.uiEvent.collect { event ->
            when (event) {
                MyPagePasswordEvent.SuccessLogin -> {
                    showStep2 = true
                }

                MyPagePasswordEvent.SuccessChange -> {
                    showChangeFinishAlert = true
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
                changePassword = myPagePasswordChangeViewModel::showCheckAlert
            )
        } else {
            MyPagePasswordCheckContent(
                emailInput = myPagePasswordChangeViewModel.emailInput,
                passwordInput = myPagePasswordChangeViewModel.passwordInput,
                loginCheck = myPagePasswordChangeViewModel::loginCheck
            )
        }
    }
    if (myPagePasswordChangeViewModel.showReCheckAlert) {
        BasicAlertDialog(
            onDismissRequest = myPagePasswordChangeViewModel::hideCheckAlert,
            properties = DialogProperties()
        ) {
            PlanUpAlertBaseContent(
                headerText = "비밀번호 재설정 확인",
                title = "새로운 비밀번호로 변경하시겠어요?",
                onDismissRequest = myPagePasswordChangeViewModel::hideCheckAlert,
                onConfirm = myPagePasswordChangeViewModel::changePassword
            )
        }
    }
    if (showChangeFinishAlert) {
        BasicAlertDialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
        ) {
            PlanUpAlertBaseSingleContent(
                headerText = "비밀번호 재설정 완료",
                title = "%s 계정의 비밀번호가 변경되었어요".format(myPagePasswordChangeViewModel.emailInput.text),
                onConfirm = onBack
            )
        }
    }
    if (myPagePasswordChangeViewModel.showLoading) {
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
    changePassword: () -> Unit
) {

    val isCorrectLength by remember {
        derivedStateOf { newPasswordInput.text.length in 8..20 }
    }
    val isCorrectDigit by remember {
        derivedStateOf { newPasswordInput.text.any { it.isDigit() } }
    }
    val isCorrectSpecial by remember {
        derivedStateOf { newPasswordInput.text.any { !it.isLetterOrDigit() } }
    }

    val enabledButton by remember {
        derivedStateOf {
            isCorrectLength && isCorrectDigit && isCorrectSpecial && newPasswordInput.text == newPasswordReInput.text
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
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
                            isValidate = newPasswordInput.text.isNotEmpty() && newPasswordInput.text == newPasswordReInput.text
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