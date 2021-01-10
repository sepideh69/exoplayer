package com.example.exoplayer.presentation.player

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Timeline

interface PlayerCallBack {

    fun onPlayerError(error: ExoPlaybackException)
    fun onIdleState()
    fun onBufferingState()
    fun onReadyState()
    fun onEndedState()
    fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int)
    fun onTimelineChanged(timeline: Timeline, reason: Int)

}