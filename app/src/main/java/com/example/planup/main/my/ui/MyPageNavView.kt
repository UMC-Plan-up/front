package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planup.component.TopHeader
import kotlinx.serialization.Serializable

sealed interface MyPageRoute {
    @Serializable
    data object Main : MyPageRoute

    @Serializable
    data object EditNickName : MyPageRoute

    @Serializable
    data object ChangeEmail : MyPageRoute

    @Serializable
    data object ChangePassword : MyPageRoute

    @Serializable
    data object LinkKakao : MyPageRoute

    @Serializable
    data object OtherAccount : MyPageRoute

    @Serializable
    data object ManageBlockFriend : MyPageRoute

    @Serializable
    data object Policy : MyPageRoute
}

@Composable
fun MyPageNavView() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MyPageRoute.Main
    ) {
        composable<MyPageRoute.Main> {
            MyPageView(
                navigateRoute = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
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
        composable<MyPageRoute.ChangeEmail> {

        }
        composable<MyPageRoute.ChangePassword> {

        }
        composable<MyPageRoute.LinkKakao> {

        }
        composable<MyPageRoute.OtherAccount> {

        }
        composable<MyPageRoute.ManageBlockFriend> {

        }
        composable<MyPageRoute.Policy> {

        }
    }
}