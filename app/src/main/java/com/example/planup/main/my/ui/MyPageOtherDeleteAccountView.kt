package com.example.planup.main.my.ui

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.R
import com.example.planup.component.PlanUpButton
import com.example.planup.extension.findActivity
import com.example.planup.login.ui.LoginActivityNew
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.my.ui.common.RoutePageDefault
import com.example.planup.main.my.ui.viewmodel.MyPageDeleteAccountViewModel
import com.example.planup.main.my.ui.viewmodel.MyPageInfoViewModel
import com.example.planup.theme.Black200
import com.example.planup.theme.Black300
import com.example.planup.theme.Typography

@Composable
fun MyPageOtherDeleteAccountView(
    onBack: () -> Unit,
    myPageInfoViewModel: MyPageInfoViewModel,
    mainSnackbarViewModel: MainSnackbarViewModel,
    myPageDeleteAccountViewModel: MyPageDeleteAccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val nickName by myPageInfoViewModel.nickName.collectAsState()
    MyPageOtherDeleteAccountContent(
        onBack = onBack,
        nickName = nickName,
        withdraw = { reason ->
            myPageDeleteAccountViewModel.withDraw(
                reason,
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

@Composable
private fun MyPageOtherDeleteAccountContent(
    onBack: () -> Unit,
    nickName: String,
    withdraw: (reason: String) -> Unit
) {
    var checkButton by rememberSaveable {
        mutableStateOf(false)
    }

    var reason by rememberSaveable {
        mutableStateOf("")
    }

    RoutePageDefault(
        onBack = onBack
    ) {
        Spacer(Modifier.height(6.dp))

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.mypage_other_delete),
            style = Typography.Semibold_3XL
        )

        Column(
            modifier = Modifier
                .imePadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text(
                        text = buildAnnotatedString {
                            append(nickName)
                            appendLine("님,")
                            append("정말 Plan-Up을 탈퇴하시겠어요?")
                        },
                        style = Typography.Medium_XL
                    )
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_notice),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                            Text(
                                text = stringResource(R.string.delete_account_notice1),
                                style = Typography.Medium_S
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_notice),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                            Text(
                                text = stringResource(R.string.delete_account_notice2),
                                style = Typography.Medium_S
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        checkButton = !checkButton
                                    }
                                ),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = if (checkButton) {
                                    painterResource(R.drawable.ic_box_checked)
                                } else {
                                    painterResource(R.drawable.ic_box_unchecked)
                                },
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                            Text(
                                text = stringResource(R.string.delete_account_agreement),
                                style = Typography.Medium_SM
                            )
                        }
                    }
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.delete_account_reason
                            ),
                            style = Typography.Medium_L
                        )
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(125.dp),
                            value = reason,
                            onValueChange = {
                                reason = it
                            },
                            textStyle = Typography.Medium_S,
                            decorationBox = { innerTextField ->
                                Column {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .border(1.dp, Black200, RoundedCornerShape(4.dp))
                                            .padding(
                                                horizontal = 12.dp, vertical = 10.dp
                                            )
                                    ) {
                                        innerTextField()
                                        if (reason.isEmpty()) {
                                            Text(
                                                text = stringResource(R.string.delete_account_feedback),
                                                style = Typography.Medium_S,
                                                color = Black300
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
            PlanUpButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.btn_close_account),
                enabled = checkButton,
                onClick = {
                    withdraw(reason)
                }
            )
        }
    }
}
