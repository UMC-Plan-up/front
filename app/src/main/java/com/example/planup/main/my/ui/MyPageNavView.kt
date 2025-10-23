package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.planup.main.MainSnackbarViewModel
import kotlinx.serialization.Serializable

sealed interface MyPageRoute {

    @Serializable
    data object Main : MyPageRoute

    object Profile {

        @Serializable
        data object EditNickName : MyPageRoute
    }

    object Account {
        @Serializable
        data object ChangeEmail : MyPageRoute

        @Serializable
        data object ChangePassword : MyPageRoute

        @Serializable
        data object LinkKakao : MyPageRoute

        @Serializable
        data object Other : MyPageRoute

        object OtherMenu {
            @Serializable
            data object Logout : MyPageRoute

            @Serializable
            data object DeleteAccount : MyPageRoute
        }
    }

    object Friend {

        @Serializable
        data object ManageBlockFriend : MyPageRoute
    }

    object Service {
        @Serializable
        data object Policy : MyPageRoute
    }

}

@Composable
fun MyPageNavView(
    mainSnackbarViewModel: MainSnackbarViewModel
) {
    val navController = rememberNavController()
    NavHost(
        modifier = Modifier.fillMaxSize(),
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
        composable<MyPageRoute.Profile.EditNickName> {
            MyPageNickNamEditView(
                onBack = navController::navigateUp,
                mainSnackbarViewModel = mainSnackbarViewModel
            )
        }
        composable<MyPageRoute.Account.ChangeEmail> {
            Button(
                onClick = {
                    mainSnackbarViewModel.updateMessage("123")
                }
            ) {
                Text(text = "test")
            }
        }
        composable<MyPageRoute.Account.ChangePassword> {

        }
        composable<MyPageRoute.Account.LinkKakao> {

        }
        composable<MyPageRoute.Account.Other> {
            MyPageOtherView(
                onBack = navController::navigateUp,
                navigateLogout = {
                    navController.navigate(MyPageRoute.Account.OtherMenu.Logout) {
                        launchSingleTop = true
                    }
                },
                navigateDelete = {
                    navController.navigate(MyPageRoute.Account.OtherMenu.DeleteAccount) {
                        launchSingleTop = true
                    }
                }
            )
        }
        dialog<MyPageRoute.Account.OtherMenu.Logout> {
            LogoutView(
                onDismissRequest = navController::navigateUp,
                mainSnackbarViewModel = mainSnackbarViewModel
            )
        }
        composable<MyPageRoute.Account.OtherMenu.DeleteAccount> {

        }
        composable<MyPageRoute.Friend.ManageBlockFriend> {

        }
        composable<MyPageRoute.Service.Policy> {

        }
    }
}