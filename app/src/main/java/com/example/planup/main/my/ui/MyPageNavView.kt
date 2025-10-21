package com.example.planup.main.my.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    data object Other : MyPageRoute

    @Serializable
    data object DeleteAccount : MyPageRoute

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
            MyPageNickNamEditView(
                onBack = navController::navigateUp
            )
        }
        composable<MyPageRoute.ChangeEmail> {

        }
        composable<MyPageRoute.ChangePassword> {

        }
        composable<MyPageRoute.LinkKakao> {

        }
        composable<MyPageRoute.Other> {
            MyPageOtherView(
                onBack = navController::navigateUp,
                navigateDelete = {
                    navController.navigate(MyPageRoute.DeleteAccount) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<MyPageRoute.DeleteAccount> {

        }
        composable<MyPageRoute.ManageBlockFriend> {

        }
        composable<MyPageRoute.Policy> {

        }
    }
}