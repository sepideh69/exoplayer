package com.example.exoplayer.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.exoplayer.data.SubtitleEntity
import com.example.exoplayer.data.VideoSource
import com.example.exoplayer.presentation.AppConstant
import com.example.exoplayer.presentation.player.config.ControlViewConfig
import com.example.exoplayer.presentation.player.config.MediaItemConfig
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes

class IntentUtil() {


    companion object {
        const val VIDEO_SOURCE_EXTRA = "VIDEO_SOURCE"
        const val MEDIA_ITEM_CONFIG_EXTRA = "MEDIA_ITEM_CONFIG"
        const val CONTROL_VIEW_CONFIG_EXTRA = "CONTROL_VIEW_CONFIG"

    }


    fun addToIntent(
        intent: Intent,
        videoSource: VideoSource,
        mediaItemConfig: MediaItemConfig = MediaItemConfig(),
        controlViewConfig: ControlViewConfig? = null

    ) {

        intent.putExtra(
            MEDIA_ITEM_CONFIG_EXTRA,
            mediaItemConfig
        )
        intent.putExtra(VIDEO_SOURCE_EXTRA, videoSource)
        if (controlViewConfig != null)
            intent.putExtra(
                CONTROL_VIEW_CONFIG_EXTRA,
                controlViewConfig
            )

    }


}