package com.example.planup.onboarding

import android.content.ClipData
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.onboarding.component.OnBoardingTextField
import com.example.planup.onboarding.model.ShareTypeModel
import com.example.planup.theme.Black300
import com.example.planup.theme.SemanticB4
import com.example.planup.theme.Typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OnBoardingShareCodeScreen(
    inviteCode: String,
    onNext: () -> Unit,
    onShareKakao: () -> Unit,
    onShareSMS: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val clipBoard = LocalClipboard.current

    val codeState = rememberTextFieldState(inviteCode)
    val shareTypes = remember { ShareTypeModel.entries.toList() }
    var isShareMenuOpen by remember { mutableStateOf(false) }

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
            text = stringResource(R.string.invite_code_share),
            style = Typography.Medium_2XL
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            OnBoardingTextField(
                modifier = Modifier
                    .height(40.dp)
                    .weight(1.0f),
                state = codeState,
                enabled = false,
                textStyle = Typography.Medium_SM.copy(color = Black300)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                PlanUpButton(
                    modifier = Modifier
                        .size(
                            width = 80.dp,
                            height = 40.dp
                        ),
                    title = stringResource(R.string.btn_share),
                    onClick = { isShareMenuOpen = !isShareMenuOpen }
                )

                ShareDropBox(
                    modifier = Modifier,
                    items = shareTypes,
                    isExpended = isShareMenuOpen,
                    onClickItem = {
                        when (it) {
                            ShareTypeModel.Kakao -> {
                                onShareKakao()
                            }

                            ShareTypeModel.Sms -> {
                                onShareSMS()
                            }

                            ShareTypeModel.Copy -> {
                                // 클립보드로 복사
                                scope.launch {
                                    clipBoard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "plain text",
                                                state.inviteCode
                                            )
                                        )
                                    )
                                }
                            }
                        }
                        isShareMenuOpen = false
                    },
                    onDismissRequest = {
                        isShareMenuOpen = false
                    },
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.0f))

        PlanUpButton(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.btn_next),
            onClick = onNext
        )

        Spacer(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(12.dp)
        )
    }
}

@Composable
fun ShareDropBox(
    items: List<ShareTypeModel>,
    onClickItem: (ShareTypeModel) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isExpended: Boolean = false,
) {
    DropdownMenu(
        modifier = modifier,
        expanded = isExpended,
        containerColor = Color.White,
        border = BorderStroke(1.dp, SemanticB4),
        onDismissRequest = onDismissRequest
    ) {
        items.forEach { type ->
            DropdownMenuItem(
                modifier = Modifier
                    .height(32.dp),
                text = {
                    Text(
                        text = type.getText(),
                        style = Typography.Medium_S.copy(color = Black300)
                    )
                },
                onClick = { onClickItem(type) }
            )
        }
    }
}


@Preview
@Composable
private fun OnBoardingShareCodeScreenPreview() {
    OnBoardingShareCodeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        inviteCode = "PLANUPDEMODAY",
        onNext = {},
        onShareKakao = {},
        onShareSMS = {}
    )
}