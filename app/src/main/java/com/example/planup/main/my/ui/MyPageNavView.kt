package com.example.planup.main.my.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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

    @Serializable
    data class PolicyDetail(
        val url: String
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
        composable<MyPageRoute.EditNickName>(
            enterTransition = { slideHorizontallyTransition() },
            exitTransition = { slideHorizontallyExitTransition() },
            popEnterTransition = { slideHorizontallyPopEnterTransition() },
            popExitTransition = { slideHorizontallyPopExitTransition() }
        ) {
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
        composable<MyPageRoute.Policy>(
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
        composable<MyPageRoute.PolicyDetail>(
            enterTransition = { slideHorizontallyTransition() },
            exitTransition = { slideHorizontallyExitTransition() },
            popEnterTransition = { slideHorizontallyPopEnterTransition() },
            popExitTransition = { slideHorizontallyPopExitTransition() }
        ) { backstackEntry ->
            val detail = backstackEntry.toRoute<MyPageRoute.PolicyDetail>()
            MyPagePolicyDetailView(
                onBack = navController::navigateUp,
                url = detail.url
            )
        }
    }
}