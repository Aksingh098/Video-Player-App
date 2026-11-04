package com.example.videoplayerapp.ui.utils

import java.util.Locale

fun formatDuration(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds/60) % 60
    val hours = (totalSeconds / 3600)

    return if(hours>0){
        String.format(Locale.getDefault(),"%d:%02d:%02d", hours,minutes,seconds)
    }else{
        String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds)
    }

}

fun formatSize(bytes: Long):String{
    val kb = bytes/1024.0
    val mb = kb/1024.0
    val gb = mb/1024.0

    return when{
        gb >=1 -> String.format(Locale.getDefault(), "%.2f GB", gb)
        mb >=1 -> String.format(Locale.getDefault(), "%.2f MB", mb)
        else -> String.format(Locale.getDefault(), "%.2f KB", kb)
    }
}