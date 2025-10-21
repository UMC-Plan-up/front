package com.example.planup.main.my.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.R
import com.example.planup.component.PlanUpButton
import com.example.planup.main.my.ui.common.RoutePageDefault
import com.example.planup.main.my.ui.viewmodel.MyPageNickNameEditViewModel
import com.example.planup.theme.Black200
import com.example.planup.theme.Black300
import com.example.planup.theme.Black400
import com.example.planup.theme.Red200
import com.example.planup.theme.SemanticR1
import com.example.planup.theme.Typography
import com.example.planup.theme.fontColor

@Composable
fun MyPageNickNamEditView(
    onBack: () -> Unit,
    myPageNickNameEditViewModel: MyPageNickNameEditViewModel = hiltViewModel()
) {
    val snackBarHost = remember { SnackbarHostState() }
    LaunchedEffect(myPageNickNameEditViewModel.errorMsg) {
        if (myPageNickNameEditViewModel.errorMsg.isNotEmpty()) {
            snackBarHost.currentSnackbarData?.dismiss()
            snackBarHost.showSnackbar(myPageNickNameEditViewModel.errorMsg)
            myPageNickNameEditViewModel.clearErrorMsg()
        }
    }
    Box(
        modifier = Modifier
            .imePadding()
    ) {
        MyPageNickNamEditContent(
            onBack = onBack,
            newName = myPageNickNameEditViewModel.newName,
            updateNewName = myPageNickNameEditViewModel::updateName,
            isError = myPageNickNameEditViewModel.isError,
            isNameError = myPageNickNameEditViewModel.isNameError,
            buttonEnabled = myPageNickNameEditViewModel.completeEnabled,
            completeAction = {
                myPageNickNameEditViewModel.changeNickname(
                    onSuccess = {
                        onBack()
                    }
                )
            }
        )
        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHost,
            snackbar = {
                Snackbar(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 44.dp)
                    ,
                    containerColor = Black300,
                    shape = RoundedCornerShape(6.dp),
                ) {
                    Text(
                        text = it.visuals.message,
                        style = Typography.Medium_SM,
                        color = fontColor
                    )
                }
            }
        )
    }
}

@Composable
private fun MyPageNickNamEditContent(
    onBack: () -> Unit = {},
    newName: String,
    updateNewName: (String) -> Unit,
    isError: Boolean,
    isNameError: Boolean,
    buttonEnabled: Boolean,
    completeAction: () -> Unit
) {
    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_nickname)
    ) {
        Spacer(Modifier.height(40.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Text(
                    text = stringResource(R.string.nickname_title),
                    style = Typography.Medium_L
                )
                BasicTextField(
                    value = newName,
                    onValueChange = {
                        updateNewName(it)
                    },
                    singleLine = true,
                    textStyle = Typography.Medium_L.copy(
                        color = Black400
                    ),
                    decorationBox = { innerTextField ->
                        Column {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(41.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                innerTextField()
                                if (newName.isEmpty()) {
                                    Text(
                                        text = "닉네임을 입력하세요",
                                        style = Typography.Medium_L,
                                        color = Black200
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = if (isError) {
                                    Red200
                                } else {
                                    Black400
                                }
                            )
                            if (isNameError) {
                                Text(
                                    text = stringResource(R.string.error_under_twenty_word),
                                    color = SemanticR1,
                                    style = Typography.Medium_XS
                                )
                            }
                        }
                    }
                )
            }
            PlanUpButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.btn_complete),
                enabled = buttonEnabled,
                completeAction
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageNickNamEditContentPreview() {
    MyPageNickNamEditContent(
        onBack = {},
        newName = "",
        updateNewName = {},
        isError = false,
        isNameError = false,
        buttonEnabled = false,
        completeAction = {}
    )
}