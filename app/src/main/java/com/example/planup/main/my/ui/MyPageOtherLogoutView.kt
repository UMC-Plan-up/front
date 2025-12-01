package com.example.planup.main.my.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.R
import com.example.planup.component.PlanUpAlertBaseContent
import com.example.planup.extension.findActivity
import com.example.planup.login.ui.LoginActivityNew
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.my.ui.viewmodel.MyPageLogoutViewModel

@Composable
fun MyPageOtherLogoutView(
    onDismissRequest: () -> Unit,
    mainSnackbarViewModel: MainSnackbarViewModel,
    myPageLogoutViewModel: MyPageLogoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    PlanUpAlertBaseContent(
        title = stringResource(R.string.popup_ask_logout),
        onDismissRequest = onDismissRequest,
        onConfirm = {
            myPageLogoutViewModel.logout(
                onSuccess = {
                    val activity = context.findActivity()
                    activity?.let {
                        val intent = Intent(context, LoginActivityNew::class.java)
                        it.finishAffinity()
                        context.startActivity(intent)
                    }
                },
                onFail = { message ->
                    mainSnackbarViewModel.updateErrorMessage(message)
                }
            )
        }
    )
}

