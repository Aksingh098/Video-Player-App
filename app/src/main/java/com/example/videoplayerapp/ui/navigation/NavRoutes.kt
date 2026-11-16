package com.example.videoplayerapp.ui.navigation

import  kotlinx.serialization.Serializable
@Serializable
sealed class MyNavRoutes {

    @Serializable
    object LocalMedia: MyNavRoutes()

    @Serializable
    object Home: MyNavRoutes()

    @Serializable
    data class PlayerScreen(val videoId: Long): MyNavRoutes()


}