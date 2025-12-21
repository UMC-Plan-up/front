package com.example.planup.onboarding

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Composable
fun OnboardNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = OnBoardTermRoute::class,
    ) {
        composable<OnBoardTermRoute> {
            OnBoardingTermScreen(
                modifier = modifier,
                state = state,
                onNext = {
                    if(viewModel.verifyRequiredTerm(it))
                        navController.navigate(OnBoardIdRoute)
                }
            )
        }

        composable<OnBoardIdRoute> {
            OnBoardingIdScreen(
                modifier = modifier,
                state = state,
                onNext = {}
            )
        }

        composable<OnBoardPasswordRoute> {
            Button(
                modifier = modifier
                    .wrapContentWidth()
                    .height(50.dp),
                onClick = {
                    navController.navigate(OnBoardVerificationRoute)
                }
            ) {
                Text(text = "OnBoardPasswordRoute")
            }

        }
        composable<OnBoardVerificationRoute> {
            Button(
                modifier = modifier
                    .wrapContentWidth()
                    .height(50.dp),
                onClick = {
                    navController.navigate(OnBoardProfileRoute)
                }
            ) {
                Text(text = "OnBoardVerificationRoute")
            }

        }

        composable<OnBoardProfileRoute> {
            Button(
                modifier = modifier
                    .wrapContentWidth()
                    .height(50.dp),
                onClick = {
                    navController.navigate(OnBoardShareFriendCodeRoute)
                }
            ) {
                Text(text = "OnBoardProfileRoute")
            }

        }

        composable<OnBoardShareFriendCodeRoute> {
            Button(
                modifier = modifier
                    .wrapContentWidth()
                    .height(50.dp),
                onClick = {
                    navController.navigate(OnBoardShareFriendCodeRoute)
                }
            ) {
                Text(text = "OnBoardShareFriendCodeRoute")
            }

        }

        composable<OnBoardShareFriendCodeRoute> {
            Button(
                modifier = modifier
                    .wrapContentWidth()
                    .height(50.dp),
                onClick = {
                    navController.navigate(OnBoardShareInviteRoute)
                }
            ) {
                Text(text = "OnBoardShareFriendCodeRoute")
            }

        }

        composable<OnBoardShareInviteRoute> {
            Button(
                modifier = modifier
                    .wrapContentWidth()
                    .height(50.dp),
                onClick = {
                    println("last")
                }
            ) {
                Text(text = "OnBoardShareInviteRoute")
            }

        }
    }
}

@Serializable
object OnBoardTermRoute

@Serializable
object OnBoardIdRoute

@Serializable
object OnBoardPasswordRoute

@Serializable
object OnBoardVerificationRoute

@Serializable
object OnBoardProfileRoute

@Serializable
object OnBoardShareFriendCodeRoute

@Serializable
object OnBoardShareInviteRoute