package com.example.planup.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.onboarding.component.OnBoardingSecureTextField
import com.example.planup.theme.Black250
import com.example.planup.theme.Blue200
import com.example.planup.theme.Typography

@Composable
fun OnBoardingPasswordScreen(
    modifier: Modifier = Modifier,
    state: OnBoardingState,
    onNext: (String) -> Unit,
    validatePasswordFormat: (String) -> Unit,
) {
    val passwordState = rememberTextFieldState("as")
    val confirmState = rememberTextFieldState("asdf")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(
                    top = 42.dp,
                    start = 6.dp,
                    end = 6.dp
                ),
            text = stringResource(R.string.signup_password_input),
            style = Typography.Medium_2XL
        )

        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 46.dp),
                text = stringResource(R.string.signup_password_label_input),
                style = Typography.Semibold_S
            )

            OnBoardingSecureTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                state = passwordState,
                inputTransformation = InputTransformation {
                    validatePasswordFormat(toString())
                },
                placeHolder = stringResource(R.string.hint_password)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ValidationCheckItem(
                    text = stringResource(R.string.signup_password_condition_length),
                    validated = state.isValidPasswordLength
                )
                ValidationCheckItem(
                    text = stringResource(R.string.signup_password_condition_complex),
                    validated = state.isComplexPassword
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(R.string.signup_password_label_confirm),
                style = Typography.Semibold_S
            )

            OnBoardingSecureTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                state = confirmState,
                inputTransformation = InputTransformation {
//                validateEmailFormat(toString())
                },
                placeHolder = stringResource(R.string.hint_password)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ValidationCheckItem(
                    text = stringResource(R.string.signup_password_condition_match),
                    validated =
                        passwordState.text.isNotBlank() && passwordState.text == confirmState.text
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.0f))

        PlanUpButton(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.btn_next),
            onClick = {
                onNext(confirmState.text.toString())
            },
            enabled =
                passwordState.text.isNotBlank() && passwordState.text == confirmState.text &&
                        state.isValidPasswordLength && state.isComplexPassword
        )

        Spacer(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(12.dp)
        )
    }
}

@Composable
private fun ValidationCheckItem(
    modifier: Modifier = Modifier,
    text: String,
    validated: Boolean = false,
) {
    val color = if (validated) Blue200 else Black250

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_check),
            contentDescription = null,
            tint = color
        )
        Text(
            text = text,
            style = Typography.Medium_XS.copy(color = color)
        )
    }
}


@Preview
@Composable
private fun OnBoardingPasswordScreenPreview() {
    OnBoardingPasswordScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        state = OnBoardingState(),
        onNext = {},
        validatePasswordFormat = {}
    )
}