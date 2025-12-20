package com.example.planup.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.PlanUpCheckbox
import com.example.planup.component.PlanUpCheckboxDefault
import com.example.planup.component.button.PlanUpButton
import com.example.planup.onboarding.component.OnBoardingProgressBar
import com.example.planup.onboarding.model.TermModel
import com.example.planup.theme.Black100
import com.example.planup.theme.Black250
import com.example.planup.theme.SemanticB4
import com.example.planup.theme.Typography

@Composable
fun OnBoardingTermScreen(
    modifier: Modifier = Modifier,
    state: OnBoardingState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        OnBoardingProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp
                ),
            progress = state.step.getFloatProgress()
        )

        OnBoardingTermBody(
            modifier = Modifier.weight(1.0f),
            terms = state.terms
        )


        OnBoardingTermTail(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun OnBoardingTermBody(
    modifier: Modifier = Modifier,
    terms: List<TermModel>
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .padding(
                    top = 42.dp,
                    start = 4.dp,
                    end = 4.dp
                ),
            text = stringResource(R.string.agreement_title),
            style = Typography.Medium_2XL
        )

        PlanUpCheckbox(
            modifier = Modifier
                .wrapContentHeight()
                .padding(
                    top = 42.dp,
                    start = 4.dp,
                    end = 4.dp
                ),
            text = stringResource(R.string.agreement_select_all),
            onCheckedChange = {},
            style = PlanUpCheckboxDefault.copy(
                checkboxSize = 18.dp,
                textStyle = Typography.Regular_M
            )
        )

        Text(
            modifier = Modifier
                .padding(
                    top = 4.dp,
                    start = 26.dp
                ),
            text = stringResource(R.string.agreement_optional_notice),
            color = Black250,
            style = Typography.Medium_S,
        )

        Spacer(
            modifier = Modifier
                .padding(top = 14.dp)
                .height(1.dp)
                .fillMaxWidth()
                .background(Black100)
        )

        Column {
            terms.forEach { term ->
                OnBoardingTermItem(
                    title = term.title,
                    detail = term.content ?: "",
                    onCheckedChange = {}
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.0f))

        Text(
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            text = stringResource(R.string.privacy_notice),
            style = Typography.Medium_XS,
            color = Black250
        )
    }
}

@Composable
private fun OnBoardingTermTail(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        PlanUpButton(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.btn_next),
            onClick = {}
        )
    }
}

@Composable
private fun OnBoardingTermItem(
    title: String,
    detail: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDetail by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlanUpCheckbox(
                text = title,
                onCheckedChange = onCheckedChange,
                style = PlanUpCheckboxDefault.copy(
                    checkboxSize = 12.dp
                )
            )
            if (detail.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .clickable(onClick = { showDetail = !showDetail }),
                    text = "자세히",
                    style = Typography.Regular_S.copy(textDecoration = TextDecoration.Underline)
                )
            }
        }
        if(showDetail) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        start = 12.dp
                    )
                    .wrapContentHeight()
                    .background(SemanticB4)
            ) {
                Text(
                    text = detail,
                    style = Typography.Regular_S
                )
            }
        }
    }
}

@Preview
@Composable
private fun OnBoardingTermScreenPreview() {
    OnBoardingTermScreen(
        modifier = Modifier.background(Color.White),
        state = OnBoardingState(
            terms = listOf(
                TermModel(
                    id = 1,
                    title = "필수이용약관1",
                    content = "세부사항1",
                    isRequired = true
                ),
                TermModel(
                    id = 1,
                    title = "선택이용약관2",
                    content = "세부사항2",
                    isRequired = false
                ),
                TermModel(
                    id = 1,
                    title = "세부사항 없는 이용약관",
                    content = "세부사항",
                    isRequired = false
                )
            )
        )
    )
}

@Preview
@Composable
private fun OnBoardingTermItemPreview() {
    Box(modifier = Modifier
        .padding(12.dp)
        .background(Color.White)) {
        OnBoardingTermItem(
            title = "개인정보 처리방침",
            detail = "약관 세부사항",
            onCheckedChange = {},
        )
    }
}