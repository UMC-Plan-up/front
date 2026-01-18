package com.example.planup.onboarding

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class InviteCodeActivity : AppCompatActivity() {
    val viewModel: InviteCodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val inviteCode by viewModel.inviteCode.collectAsStateWithLifecycle()

            NavHost(
                modifier = Modifier,
                navController = navController,
                startDestination = OnBoardShareCodeRoute::class
            ) {

                composable<OnBoardShareCodeRoute> {
                    Column {
                        OnBoardingShareCodeScreen(
                            modifier = Modifier,
                            inviteCode = inviteCode,
                            onNext = {
                                navController.navigate(OnBoardInputCodeRoute::class)
                            },
                            onShareKakao = {
                                // 카카오톡 공유
                            },
                            onShareSMS = {

                            }
                        )
                    }
                }

                composable<OnBoardInputCodeRoute> {
                    Column {
                        OnBoardingInputCodeScreen(
                            modifier = Modifier,
                            onNext = {

                            },
                            onCodeSubmitted = {

                            }
                        )
                    }
                }
            }
        }
    }
}

@Serializable
object OnBoardShareCodeRoute

@Serializable
object OnBoardInputCodeRoute