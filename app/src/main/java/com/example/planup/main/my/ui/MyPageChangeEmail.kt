package com.example.planup.main.my.ui

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.planup.R
import com.example.planup.component.PlanUpButton
import com.example.planup.main.my.ui.common.RoutePageDefault
import com.example.planup.theme.Black300
import com.example.planup.theme.Blue300
import com.example.planup.theme.Typography

@Composable
fun MyPageChangeEmailView(
    onBack: () -> Unit = {}
) {
    MyPageChangeEmailContent(
        onBack = onBack
    )
}

@Composable
fun MyPageChangeEmailContent(
    onBack: () -> Unit = {}
) {
    var email by remember {
        mutableStateOf("")
    }

    val enableButton by remember(email) {
        derivedStateOf {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    var showDropDownMenu by remember {
        mutableStateOf(false)
    }
    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_email)
    ) {
        Spacer(Modifier.height(40.dp))
        Column(
            modifier = Modifier.fillMaxSize()
                .imePadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.new_email),
                        style = Typography.Medium_L
                    )
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

                        BasicTextField(
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize = coordinates.size
                                },
                            value = email,
                            onValueChange = {
                                email = it
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email
                            ),
                            singleLine = true,
                            textStyle = Typography.Medium_SM,
                            decorationBox = { innerTextField ->
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .border(1.dp, Blue300, RoundedCornerShape(6.dp))
                                        .padding(
                                            horizontal = 6.dp,
                                            vertical = 2.dp
                                        ),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    innerTextField()
                                    if (email.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.email_enter),
                                            style = Typography.Medium_SM,
                                            color = Black300
                                        )
                                    }
                                    IconButton(
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                        onClick = {
                                            showDropDownMenu = !showDropDownMenu
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_arrow_down_email),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )

                        if (showDropDownMenu) {
                            Popup(
                                offset = with(LocalDensity.current) {
                                    IntOffset(
                                        x = textFieldSize.width - 200, // dropdown width 만큼 조정 가능
                                        y = textFieldSize.height
                                    )
                                },
                                onDismissRequest = {
                                    showDropDownMenu = false
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .width(118.dp)
                                        .padding(10.dp)
                                ) {
                                    listOf(
                                        stringResource(R.string.dropdown_gmail),
                                        stringResource(R.string.dropdown_naver),
                                        stringResource(R.string.dropdown_kakao),
                                    ).forEach {
                                        Text(
                                            text = it,
                                            style = Typography.Medium_S,
                                        )
                                    }
                                    Text("123456")
                                    Text("456")
                                    Text("789")
                                }
                            }
                        }
                    }
                }
            }
            PlanUpButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.btn_get_link),
                enabled = enableButton,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPageChangeEmailContentPreview() {
    MyPageChangeEmailContent()
}