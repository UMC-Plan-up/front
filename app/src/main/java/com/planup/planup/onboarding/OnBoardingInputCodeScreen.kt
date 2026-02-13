package com.planup.planup.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.planup.planup.R
import com.planup.planup.component.button.PlanUpButton
import com.planup.planup.onboarding.component.OnBoardingTextField
import com.planup.planup.theme.Black300
import com.planup.planup.theme.Typography

@Composable
fun OnBoardingInputCodeScreen(
    onNext: () -> Unit,
    onCodeSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val codeState = rememberTextFieldState()

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
            text = stringResource(R.string.invite_code_input),
            style = Typography.Medium_2XL.copy(Black300)
        )

        OnBoardingTextField(
            modifier = Modifier
                .padding(top = 16.dp)
                .height(40.dp),
            state = codeState,
            textStyle = Typography.Medium_SM.copy(color = Black300),
            placeHolder = stringResource(R.string.invite_code_input_hint)
        )

        PlanUpButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            enabled = codeState.text.isNotBlank(),
            title = stringResource(R.string.btn_input),
            onClick = { onCodeSubmitted(codeState.text.toString()) }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        onNext()
                    },
                text = stringResource(R.string.next_input),
                style = Typography.Semibold_S,
                textDecoration = TextDecoration.Underline
            )
        }
    }

}

@Preview
@Composable
private fun OnBoardingInputCodeScreenPreview() {
    OnBoardingInputCodeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        onNext = {},
        onCodeSubmitted = {}
    )
}