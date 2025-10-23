package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planup.main.MainActivity
import com.example.planup.main.MainSnackbarViewModel
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
    val context = LocalContext.current
    val mainSnackbarViewModel: MainSnackbarViewModel = hiltViewModel(context as MainActivity)

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
        composable<MyPageRoute.EditNickName> {
            MyPageNickNamEditView(
                onBack = navController::navigateUp,
                mainSnackbarViewModel = mainSnackbarViewModel
            )
        }
        composable<MyPageRoute.ChangeEmail> {
            Button(
                onClick = {
                    mainSnackbarViewModel.updateMessage("123")
                }
            ) {
                Text(text = "test")
            }
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