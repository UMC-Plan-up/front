package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.R
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
    LaunchedEffect(myPagePasswordChangeViewModel) {
        myPagePasswordChangeViewModel.uiEvent.collect { event ->
            when (event) {
                MyPagePasswordEvent.SuccessLogin -> {

                }
                is MyPagePasswordEvent.Error -> {
                    mainSnackbarViewModel.updateErrorMessage(event.message)
                }
            }
        }
    }
    MyPagePasswordChangeContent(
        onBack = onBack,
        emailInput = myPagePasswordChangeViewModel.emailInput,
        passwordInput = myPagePasswordChangeViewModel.passwordInput
    )
}

@Composable
fun MyPagePasswordChangeContent(
    onBack: () -> Unit = {},
    emailInput: TextFieldState,
    passwordInput: TextFieldState,
    loginCheck :() -> Unit = {}
) {
    val enabledButton by remember {
        derivedStateOf {
            emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty()
        }
    }
    MyPageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_password_check)
    ) {
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
                modifier = Modifier.fillMaxWidth()
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
}

@Preview
@Composable
fun MyPagePasswordChangeContentPreview() {
    val state1 = remember { TextFieldState() }
    val state2 = remember { TextFieldState() }

    MyPagePasswordChangeContent(
        emailInput = state1,
        passwordInput = state2
    )
}