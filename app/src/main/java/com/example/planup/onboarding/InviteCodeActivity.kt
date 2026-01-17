package com.example.planup.onboarding

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
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


            NavHost(
                modifier = Modifier,
                navController = navController,
                startDestination = OnBoardShareCodeRoute::class
            ) {
                composable<OnBoardShareCodeRoute> {

                }

                composable<OnBoardInputCodeRoute> {

                }
            }
        }
    }
}

@Serializable
object OnBoardShareCodeRoute

@Serializable
object OnBoardInputCodeRoute