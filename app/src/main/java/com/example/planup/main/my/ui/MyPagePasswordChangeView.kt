package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.main.my.ui.common.MyPageDefault

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
        categoryText = stringResource(R.string.mypage_password)
    ) {
        Spacer(Modifier.height(40.dp))
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