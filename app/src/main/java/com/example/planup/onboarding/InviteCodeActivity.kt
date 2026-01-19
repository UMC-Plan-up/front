package com.example.planup.onboarding

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planup.component.TopHeader
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

            InviteCodeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                navController = navController,
                inviteCode = inviteCode
            )
        }
    }
}

@Composable
fun InviteCodeScreen(
    inviteCode: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    Column {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = OnBoardShareCodeRoute::class
        ) {
            composable<OnBoardShareCodeRoute> {
                Column {
                    OnBoardingShareCodeScreen(
                        modifier = Modifier
                            .padding(top = 56.dp),
                        inviteCode = inviteCode,
                        onNext = {
                            navController.navigate(OnBoardInputCodeRoute)
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
                    TopHeader(
                        modifier = Modifier
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Top
                                )
                            )
                            .fillMaxWidth()
                            .height(48.dp),
                        onBackAction = {
                            navController.navigateUp()
                        },
                        title = "",
                    )
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

@Preview
@Composable
private fun InviteCodeScreenPreview() {
    InviteCodeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        inviteCode = "초대코드"
    )
}

@Serializable
object OnBoardShareCodeRoute

@Serializable
object OnBoardInputCodeRoute