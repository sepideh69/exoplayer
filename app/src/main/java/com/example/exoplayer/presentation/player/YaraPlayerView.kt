package com.example.exoplayer.presentation.player

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.exoplayer.R
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class YaraPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var exoPlayerView: PlayerView? = null
    var backBtn: ImageButton? = null
    var unLockBtn: ImageButton? = null

    var settingBtn: ImageButton? = null
    var subtitleBtn: ImageButton? = null
    var muteBtn: ImageButton? = null
    var unMuteBtn: ImageButton? = null
    var lockBtn: ImageButton? = null

    var videoTitle: TextView? = null

    fun init() {

        LayoutInflater.from(context)
            .inflate(R.layout.yara_player_view, this, true)
        setUpLayout()
    }

    private fun setUpLayout() {

        exoPlayerView = findViewById(R.id.exo_player_view)
        backBtn = findViewById(R.id.exo_back)
        unLockBtn = findViewById(R.id.exo_unLock)

        //custom
        settingBtn = findViewById(R.id.exo_setting)
        subtitleBtn = findViewById(R.id.exo_subtitle)
        muteBtn = findViewById(R.id.exo_mute)
        unMuteBtn = findViewById(R.id.exo_unMute)
        lockBtn = findViewById(R.id.exo_lock)

        videoTitle = findViewById(R.id.exo_Video_title)

    }

    fun setSubtitleImage(hasSubtitle: Boolean = false) {
        subtitleBtn?.apply {
            if (hasSubtitle)
                setImageResource(R.drawable.exo_subtitle_btn)
            else
                setImageResource(R.drawable.exo_no_subtitle_btn)

        }

    }
}