package com.example.planup.main.my.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.planup.databinding.PopupResendEmailBinding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageChangeEmailSheet(
    onDismissRequest: () -> Unit,
    resendEmail: () -> Unit,
    useKaKaoLogin: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        MyPageChangeEmailSheetContent(
            resendEmail = resendEmail,
            useKaKaoLogin = useKaKaoLogin
        )
    }
}

@Composable
private fun MyPageChangeEmailSheetContent(
    resendEmail: () -> Unit,
    useKaKaoLogin: () -> Unit
) {
    AndroidViewBinding(PopupResendEmailBinding::inflate) {
        resendEmailOption.setOnClickListener {
            resendEmail()
        }
        kakaoLoginOption.setOnClickListener {
            useKaKaoLogin()
        }
    }
}

@Composable
@Preview
private fun MyPageChangeEmailSheetContentPreview() {
    MyPageChangeEmailSheetContent(
        {},
        {}
    )
}