package com.example.videoplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.videoplayerapp.repository.VideoRepository
import com.example.videoplayerapp.ui.screens.HomeScreen
import com.example.videoplayerapp.ui.screens.LocalMediaScreen
import com.example.videoplayerapp.ui.screens.PlayerScreen
import com.example.videoplayerapp.ui.theme.VideoPlayerAppTheme
import com.example.videoplayerapp.ui.viewModel.VideoPlayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = VideoRepository(applicationContext)
        setContent {
            VideoPlayerAppTheme {
                val navController = rememberNavController()
                val viewModel: VideoPlayerViewModel = viewModel {

                    VideoPlayerViewModel(repo)
                }

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen((navController))
                    }

                    composable("localMedia") {
                        LocalMediaScreen(navController, viewModel)
                    }
                    composable("player/{videoId}", listOf(navArgument("videoId") {
                        type = NavType.LongType
                    })) { backStackEntry ->
                        val videoId = backStackEntry.arguments?.getLong("videoId") ?: 0L
                        PlayerScreen(
                            videoId = videoId,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }

                }

            }
        }
    }
}

