package com.example.exoplayer.presentation.player

import com.google.android.exoplayer2.MediaItem

interface OnControlViewClick {

    fun onRewClick()
    fun onSettingClick()
    fun onSubtitleClick(subtitles: ArrayList<MediaItem.Subtitle>)
    fun onBackClick()
    fun onLockClick(isLock : Boolean)



}