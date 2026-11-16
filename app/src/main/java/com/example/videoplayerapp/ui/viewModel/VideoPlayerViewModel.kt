package com.example.videoplayerapp.ui.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.videoplayerapp.model.LocalVideo
import com.example.videoplayerapp.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {

    var videos by mutableStateOf<List<LocalVideo>>(emptyList())
    var isLoading by mutableStateOf(false)


    suspend fun loadLocalVideo() {
        isLoading = true
        videos = repo.getLocalVideos()
        isLoading = false
    }


    fun getVideoUri(id: Long): Uri = repo.getVideoUriById(id)

    suspend fun deleteVideo(video: LocalVideo): Boolean {
        val success = repo.deleteVideo(video.uri)
        if (success) {
            videos = videos.filter { it.id != video.id }
        }

        return success
    }

    // for android 10+
    fun deleteVideoFromList(video: LocalVideo) {
        videos = videos.filter { it.id != video.id }
    }

}