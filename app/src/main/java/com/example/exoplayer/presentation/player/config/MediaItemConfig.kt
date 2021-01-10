package com.example.exoplayer.presentation.player.config

import android.os.Parcelable
import com.google.android.exoplayer2.MediaItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaItemConfig(
    val initAds: Boolean = false,
    val addSubtitle: Boolean = false
) : Parcelable