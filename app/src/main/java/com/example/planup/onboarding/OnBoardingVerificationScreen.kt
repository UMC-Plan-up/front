package com.example.planup.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.theme.SemanticB5
import com.example.planup.theme.SemanticB7
import com.example.planup.theme.Typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingVerificationScreen(
    modifier: Modifier = Modifier,
    state: OnBoardingState,
    onNext: () -> Unit,
    onClickResend: () -> Unit = {},
    onClickKaKao: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 26.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(
                    top = 42.dp
                ),
            text = stringResource(R.string.signup_verify_title),
            style = Typography.Medium_2XL
        )

        Text(
            modifier = Modifier
                .padding(top = 18.dp),
            text = stringResource(R.string.signup_verify_email_sent),
            style = Typography.Medium_SM
        )

        Spacer(modifier = Modifier.height(82.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlanUpButton(
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(R.string.signup_verify_email_complete),
                onClick = onNext
            )

            Text(
                modifier = Modifier
                    .padding(top = 18.dp)
                    .clickable {
                        println("lost")
                        scope.launch { sheetState.expand() }
                    },
                text = stringResource(R.string.signup_verify_email_lost),
                style = Typography.Semibold_S,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
            )
        }

        if (sheetState.isAnimationRunning || sheetState.isVisible) {
            Box {
                VerificationLostModal(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(),
                    state = sheetState,
                    scope = scope,
                    onClickResend = onClickResend,
                    onClickKaKao = onClickKaKao
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerificationLostModal(
    state: SheetState,
    onClickResend: () -> Unit,
    onClickKaKao: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = {},
        sheetState = state,
        containerColor = SemanticB7,
        dragHandle = {}
    ) {
        VerificationLostModalContent(
            modifier = modifier,
            onClickResend = onClickResend,
            onClickKaKao = onClickKaKao,
            onCancel = { scope.launch { state.hide() } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationLostModalContent(
    onClickResend: () -> Unit,
    onClickKaKao: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = 28.dp,
                vertical = 34.dp
            )
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.popup_email_title),
            style = Typography.Semibold_L
        )

        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(40.dp)
                .clickable {
                    onClickResend()
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.popup_email_again),
                style = Typography.Regular_L
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SemanticB5)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clickable {
                    onClickKaKao()
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.popup_email_kakao),
                style = Typography.Regular_L
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        PlanUpButton(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.btn_cancel),
            onClick = onCancel
        )
    }
}

@Preview
@Composable
private fun OnBoardingVerificationScreenPreview() {
    OnBoardingVerificationScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        state = OnBoardingState(),
        onNext = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun VerificationLostModalPreview() {
    VerificationLostModalContent(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .background(Color.White),
        onClickResend = {},
        onClickKaKao = {},
        onCancel = {}
    )
}