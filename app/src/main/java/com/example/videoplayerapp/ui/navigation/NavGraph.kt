package com.example.videoplayerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.videoplayerapp.ui.screens.HomeScreen
import com.example.videoplayerapp.ui.screens.LocalMediaScreen
import com.example.videoplayerapp.ui.screens.PlayerScreen
import com.example.videoplayerapp.ui.viewModel.VideoPlayerViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {

    // navController -> handle navigation
    // rememberNavController() -> Create & remember the navController


    NavHost(
        navController = navController,
        startDestination = MyNavRoutes.Home
    ) {
        composable<MyNavRoutes.Home> {
            HomeScreen(navController)
        }

        composable<MyNavRoutes.LocalMedia> {
            LocalMediaScreen(
                navController,
                viewModel = viewModel
            )
        }

        composable<MyNavRoutes.PlayerScreen> { backStackEntry ->

            val data = backStackEntry.toRoute<MyNavRoutes.PlayerScreen>()

            PlayerScreen(
                videoId = data.videoId,
                navController = navController,
                viewModel = viewModel
            )
        }

    }
}