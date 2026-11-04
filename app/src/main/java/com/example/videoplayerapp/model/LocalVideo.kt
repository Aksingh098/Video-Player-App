package com.example.videoplayerapp.model

import android.net.Uri
import java.net.URI

data class LocalVideo(
    val id: Long,
    val title: String,
    val duration: Long,
    val uri: Uri,
    val size:Long,
    val dateAdded: Long,
    val mimeType: String?

)
