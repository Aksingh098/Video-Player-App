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
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.videoplayerapp.ui.navigation.NavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VideoPlayerAppTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController
                )

            }
        }
    }
}

