package com.planup.planup.onboarding.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.planup.planup.R

enum class ShareTypeModel {
    Kakao,
    Sms,
    Copy;

    @Composable
    fun getText(): String {
        return when (this) {
            Kakao -> stringResource(R.string.share_kakao)
            Sms -> stringResource(R.string.share_sms)
            Copy -> stringResource(R.string.share_copy)
        }
    }
}