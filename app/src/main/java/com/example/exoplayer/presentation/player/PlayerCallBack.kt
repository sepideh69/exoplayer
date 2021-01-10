package com.example.exoplayer.presentation.player

import android.util.Log
import com.example.exoplayer.presentation.AppConstant.Companion.TAG
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem

interface PlayerCallBack {

    fun onPlayerError(error: ExoPlaybackException)
    fun onIdleState() {
        Log.d(TAG, "onIdleState: ")
    }

    fun onBufferingState() {
        Log.d(TAG, "onBufferingState: ")
    }

    fun onReadyState()
    fun onEndedState() {
        Log.d(TAG, "onEndedState: ")
    }

    fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int)

}