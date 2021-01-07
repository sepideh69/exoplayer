package com.example.exoplayer.presentation.player.config

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ControlViewConfig(
    val adSetting: Boolean,
    val adSubtitle :Boolean,
    val addLock: Boolean,
    val addMute: Boolean
) : Parcelable