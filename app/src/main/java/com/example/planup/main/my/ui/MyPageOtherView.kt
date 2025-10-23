package com.example.planup.main.my.ui

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.R
import com.example.planup.component.PlanUpBorderButton
import com.example.planup.component.PlanUpButton
import com.example.planup.login.ui.LoginActivityNew
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.my.ui.common.RouteMenuItem
import com.example.planup.main.my.ui.common.RoutePageDefault
import com.example.planup.main.my.ui.viewmodel.MyPageLogoutViewModel
import com.example.planup.theme.Typography

@Composable
fun MyPageOtherView(
    onBack: () -> Unit,
    navigateDelete: () -> Unit,
    mainSnackbarViewModel: MainSnackbarViewModel,
    myPageLogoutViewModel: MyPageLogoutViewModel = hiltViewModel()
) {
    var showLogoutDialog by rememberSaveable() {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.category)
    ) {
        Spacer(Modifier.height(20.dp))
        RouteMenuItem(
            title = stringResource(R.string.mypage_other_logout),
            action = {
                showLogoutDialog = true
            }
        )
        RouteMenuItem(
            title = stringResource(R.string.mypage_other_delete),
            action = navigateDelete
        )
    }
    if (showLogoutDialog) {
        LogoutDialog(
            onDismissRequest = { showLogoutDialog = false },
            logout = {
                myPageLogoutViewModel.logout(
                    onSuccess = {
                        showLogoutDialog = false
                        val intent = Intent(context, LoginActivityNew::class.java)
                        context.startActivity(intent)
                    },
                    onFail = { message ->
                        showLogoutDialog = false
                        mainSnackbarViewModel.updateMessage(message)
                    }
                )
            }
        )
    }
}


@Composable
private fun LogoutDialog(
    onDismissRequest: () -> Unit,
    logout: () -> Unit = {}
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest
    ) {
        OutlinedCard(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xffdadada)),
            colors = CardDefaults.outlinedCardColors(
                containerColor = androidx.compose.ui.graphics.Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .height(140.dp)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.popup_ask_logout),
                        style = Typography.Medium_SM
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PlanUpBorderButton(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.btn_cancel),
                        onClick = onDismissRequest
                    )
                    PlanUpButton(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.btn_ok),
                        onClick = logout
                    )
                }
            }
        }
    }
}