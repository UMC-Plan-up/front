package com.example.planup.main.my.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
        data object ManageBlock : MyPageRoute
    }

    object Service {
        @Serializable
        data object Policy : MyPageRoute

        @Serializable
        data class Detail(
            val url: String
        ) : MyPageRoute
    }

    @Serializable
    data class NotificationMarketing(
        val isAgree: Boolean,
        val nickName: String,
        val date: String
    ) : MyPageRoute
}

private fun slideHorizontallyTransition(): EnterTransition = slideInHorizontally { it }

private fun slideHorizontallyExitTransition(): ExitTransition = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(300)
)

private fun slideHorizontallyPopEnterTransition(): EnterTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300)
)

private fun slideHorizontallyPopExitTransition(): ExitTransition = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(300)
)

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
                },
                mainSnackbarViewModel = mainSnackbarViewModel
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
                    mainSnackbarViewModel.updateErrorMessage("123")
                }
            ) {
                Text(text = "test")
            }
        }
        composable<MyPageRoute.Account.ChangePassword> {
            MyPagePasswordChangeView(
                onBack = navController::navigateUp
            )
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
            MyPageOtherLogoutView(
                onDismissRequest = navController::navigateUp,
                mainSnackbarViewModel = mainSnackbarViewModel
            )
        }
        composable<MyPageRoute.Account.OtherMenu.DeleteAccount> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MyPageRoute.Main)
            }
            MyPageOtherDeleteAccountView(
                onBack = navController::navigateUp,
                myPageInfoViewModel = hiltViewModel(parentEntry),
                mainSnackbarViewModel = mainSnackbarViewModel
            )
        }
        composable<MyPageRoute.Friend.ManageBlock> {
            MyPageManageBlockFriendView(
                onBack = navController::navigateUp,
                mainSnackbarViewModel = mainSnackbarViewModel
            )
        }
        composable<MyPageRoute.Service.Policy>(
            enterTransition = { slideHorizontallyTransition() },
            exitTransition = { slideHorizontallyExitTransition() },
            popEnterTransition = { slideHorizontallyPopEnterTransition() },
            popExitTransition = { slideHorizontallyPopExitTransition() }
        ) {
            MyPagePolicyView(
                onBack = navController::navigateUp,
                routePolicy = { policyDetail ->
                    navController.navigate(policyDetail) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<MyPageRoute.Service.Detail>(
            enterTransition = { slideHorizontallyTransition() },
            exitTransition = { slideHorizontallyExitTransition() },
            popEnterTransition = { slideHorizontallyPopEnterTransition() },
            popExitTransition = { slideHorizontallyPopExitTransition() }
        ) { backstackEntry ->
            val detail = backstackEntry.toRoute<MyPageRoute.Service.Detail>()
            MyPagePolicyDetailView(
                onBack = navController::navigateUp,
                url = detail.url
            )
        }
        dialog<MyPageRoute.NotificationMarketing> { route ->
            val result = route.toRoute<MyPageRoute.NotificationMarketing>()
            MyPageNotificationMarketingResultView(
                onBack = navController::navigateUp,
                result.isAgree,
                result.nickName,
                result.date
            )
        }
    }
}