package com.planup.planup.main.my.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.planup.planup.R
import com.planup.planup.component.PlanUpAlertBaseContent
import com.planup.planup.extension.findActivity
import com.planup.planup.login.ui.LoginActivityNew
import com.planup.planup.main.MainSnackbarViewModel
import com.planup.planup.main.my.ui.viewmodel.MyPageLogoutViewModel

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

