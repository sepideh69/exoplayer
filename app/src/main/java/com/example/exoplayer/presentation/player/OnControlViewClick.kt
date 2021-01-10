package com.example.exoplayer.presentation.player

import com.google.android.exoplayer2.MediaItem

interface OnControlViewClick {

    fun onMuteClick()
    fun onUnMuteClick()
    fun onRewClick()
    fun onSettingClick()
    fun onSubtitleClick(subtitles: ArrayList<MediaItem.Subtitle>)
    fun onLockClick()
    fun onUnLockClick()
    fun onBackClick()



}