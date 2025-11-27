package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.main.my.ui.common.MyPageDefault
import com.example.planup.theme.Typography

@Composable
fun MyPageKakaoView(
    onBack :() -> Unit,
    kakaoAccount : String
) {
    MyPageKaKaoContent(
        onBack = onBack,
        account = kakaoAccount
    )
}


@Composable
private fun MyPageKaKaoContent(
    onBack :() -> Unit,
    account : String
) {
    MyPageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_kakao)
    ) {
        Spacer(Modifier.height(72.dp))

        Text(
            text = stringResource(R.string.kakao_sync,account),
            style = Typography.Medium_S
        )
    }
}