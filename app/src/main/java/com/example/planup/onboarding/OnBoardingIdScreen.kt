package com.example.planup.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.onboarding.component.OnBoardingTextField
import com.example.planup.theme.Blue200
import com.example.planup.theme.Typography

@Composable
fun OnBoardingIdScreen(
    modifier: Modifier = Modifier,
    state: OnBoardingState,
    onNext: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
) {
    val textState = rememberTextFieldState(initialText = state.email)

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
            text = stringResource(R.string.signup_id_input),
            style = Typography.Medium_2XL
        )

        Text(
            modifier = Modifier
                .padding(top = 72.dp),
            text = stringResource(R.string.signup_email),
            style = Typography.Semibold_S
        )

        OnBoardingTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            state = textState,
            inputTransformation = InputTransformation {
                onEmailChanged(toString())
            },
            placeHolder = stringResource(R.string.planup_email)
        )

        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 12.dp
                )
                .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if(!state.isValidEmailFormat) {
                Text(
                    text = stringResource(R.string.error_invalid_email),
                    style = Typography.Medium_XS,
                    color = Blue200
                )
            }
            if(state.isDuplicatedEmail) {
                Text(
                    text = stringResource(R.string.error_email_already_in_use),
                    style = Typography.Medium_XS,
                    color = Blue200
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.0f))

        PlanUpButton(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.btn_next),
            onClick = {
                onNext(textState.text.toString())
            },
            enabled = textState.text.isNotBlank() && state.isValidEmailFormat
        )

        Spacer(modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(12.dp)
        )
    }
}

@Preview
@Composable
private fun OnBoardingIdScreenPreview() {
    OnBoardingIdScreen(
        modifier = Modifier.background(Color.White),
        state = OnBoardingState(),
        onNext = {},
        onEmailChanged = {}
    )
}