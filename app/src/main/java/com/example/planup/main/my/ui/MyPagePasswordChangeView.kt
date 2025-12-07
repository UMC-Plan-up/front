package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.main.my.ui.common.MyPageDefault
import com.example.planup.theme.Typography

@Composable
fun MyPagePasswordChangeView(
    onBack: () -> Unit
) {
    MyPagePasswordChangeContent(
        onBack = onBack
    )
}

@Composable
fun MyPagePasswordChangeContent(
    onBack: () -> Unit = {}
) {
    MyPageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_password_check)
    ) {
        Text(
            text = stringResource(R.string.mypage_password_check_sub),
            style = Typography.Medium_S
        )
        Spacer(Modifier.height(20.dp))
        Column(

        ) { }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            PlanUpButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.btn_complete),
                enabled = true,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
fun MyPagePasswordChangeContentPreview() {
    MyPagePasswordChangeContent()
}