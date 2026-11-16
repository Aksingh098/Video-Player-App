package com.example.videoplayerapp.ui.screens

import android.app.Activity
import android.app.RecoverableSecurityException
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.videoplayerapp.model.LocalVideo
import com.example.videoplayerapp.ui.navigation.MyNavRoutes
import com.example.videoplayerapp.ui.utils.VideoRow
import com.example.videoplayerapp.ui.viewModel.VideoPlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalMediaScreen(navController: NavHostController, viewModel: VideoPlayerViewModel) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadLocalVideo()
    }

    var videoToDelete by remember { mutableStateOf<LocalVideo?>(null) }
    var pendingToDelete by remember { mutableStateOf<LocalVideo?>(null) }

    val scope = rememberCoroutineScope()

    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        pendingToDelete?.let { video ->
            if (result.resultCode == Activity.RESULT_OK) {

                    viewModel.deleteVideoFromList(video)

                Toast.makeText(context, "Video Deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Delete cancelled", Toast.LENGTH_SHORT).show()
            }
        }
        pendingToDelete = null


    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local Media") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back Icon"

                        )
                    }
                }
            )
        }
    ) { innerpadding ->
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerpadding),
                    contentAlignment = Alignment.Center

                ) {
                    CircularProgressIndicator()

                }

            }

            viewModel.videos.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerpadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.VideoLibrary,
                        contentDescription = "video library",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "No Video Available",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerpadding),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(viewModel.videos) { video ->
                        VideoRow(
                            video = video,
                            onClick = {
                                navController.navigate(MyNavRoutes.PlayerScreen(videoId = video.id))
                            },
                            onDelete = { videoToDelete = video }
                        )


                    }
                }
            }
        }

        videoToDelete?.let { video ->
            AlertDialog(
                onDismissRequest = { videoToDelete = null },
                title = { Text("Delete Video") },
                text = { Text("Do you want to delete \" ${video.title}\" \n This action can't be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            videoToDelete = null
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                try {
                                    val deleteRequest = MediaStore.createDeleteRequest(
                                        context.contentResolver,
                                        listOf(video.uri)
                                    )
                                    pendingToDelete = video

                                    deleteLauncher.launch(
                                        IntentSenderRequest.Builder(deleteRequest.intentSender)
                                            .build()
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Deletion Failed: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                scope.launch {
                                    try {
                                        val deleted = withContext(Dispatchers.IO) {
                                            context.contentResolver.delete(
                                                video.uri,
                                                null,
                                                null
                                            ) > 0
                                        }
                                        if (deleted) {
                                            viewModel.deleteVideoFromList(video)
                                            Toast.makeText(
                                                context,
                                                "Video Deleted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (securityException: SecurityException) {
                                        val recoverableException =
                                            securityException as? RecoverableSecurityException

                                        recoverableException?.let {
                                            pendingToDelete = video
                                            deleteLauncher.launch(
                                                IntentSenderRequest.Builder(it.userAction.actionIntent.intentSender)
                                                    .build()
                                            )
                                        } ?: run {
                                            Toast.makeText(
                                                context,
                                                "Video cannot be deleted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                scope.launch {
                                    val success = viewModel.deleteVideo(video)
                                    if (success) {
                                        Toast.makeText(context, "Video Deleted", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Video cannot be deleted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }

                        }
                    ) {
                        Text(
                            "Delete",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            videoToDelete = null
                        }
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            )
        }

    }

}