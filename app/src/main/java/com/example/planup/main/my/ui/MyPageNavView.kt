package com.example.planup.main.my.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planup.R
import com.example.planup.component.TopHeader
import kotlinx.serialization.Serializable

sealed interface MyPageRoute {
    @Serializable
    data object Main : MyPageRoute

    @Serializable
    data object EditNickName : MyPageRoute
}

@Composable
fun MyPageNavView() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MyPageRoute.Main
    ) {
        composable<MyPageRoute.Main> {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 13.dp)
                ) {
                    item {
                        RouteItem(
                            title = stringResource(R.string.mypage_nickname)
                        ) {
                            navController.navigate(MyPageRoute.EditNickName) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
        composable<MyPageRoute.EditNickName> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopHeader(
                    title = "목록",
                    onBackAction = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun RouteItem(
    title: String,
    action: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable {
                action()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title
        )
        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null
        )
    }
}