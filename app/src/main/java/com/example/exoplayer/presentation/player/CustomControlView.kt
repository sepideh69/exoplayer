package com.example.exoplayer.presentation.player

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.exoplayer.R
import com.example.exoplayer.presentation.player.config.ControlViewConfig
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class CustomControlView(
    private val view: View?,
    private val exoPlayer: SimpleExoPlayer?,
    private val onControlViewClick: OnControlViewClick
) {

    private var TAG = "my_player"
    private var playerView: PlayerView? = null
    private var backBtn : ImageButton?=null
    private var unLockBtn : ImageButton?=null



    private var config: ControlViewConfig? = null

    private var settingBtn: ImageButton? = null
    private var subtitleBtn: ImageButton? = null
    private var muteBtn: ImageButton? = null
    private var unMuteBtn: ImageButton? = null
    private var lockBtn: ImageButton? = null



    var isAllowToBack = true
    var subtitleList = ArrayList<MediaItem.Subtitle>()


    fun start(controlViewConfig: ControlViewConfig) {

        config = controlViewConfig
        setUpLayout()
        customiseController()
    }


    fun updateSubtitleImage(hasSubtitle: Boolean) {
        subtitleBtn?.apply {
            if (hasSubtitle)
                setImageResource(R.drawable.exo_subtitle_btn)
            else
                setImageResource(R.drawable.exo_no_subtitle_btn)

        }

    }


    private fun setUpLayout() {

        Log.d(TAG, "setUpLayout: custom")

        view?.apply {
            playerView = findViewById(R.id.demo_player_view)
            unLockBtn = findViewById(R.id.btn_unLock)
            backBtn = findViewById(R.id.btn_back)
        }


        playerView?.apply {
            settingBtn = findViewById(R.id.exo_setting)
            subtitleBtn = findViewById(R.id.exo_subtitle)
            muteBtn = findViewById(R.id.exo_mute)
            unMuteBtn = findViewById(R.id.exo_unMute)
            lockBtn = findViewById(R.id.exo_lock)

        }


    }

    private fun customiseController() {

        Log.d(TAG, "handleCustomControlView: $config")

        config?.let { config ->

            settingBtn?.apply {
                visibility = if (config.adSetting) View.VISIBLE else View.GONE
                setOnClickListener {

                    onControlViewClick.onSettingClick()
                }

            }
            subtitleBtn?.apply {
                visibility = if (config.adSubtitle) View.VISIBLE else View.GONE
                setOnClickListener {

                    onControlViewClick.onSubtitleClick(subtitleList)
                }

            }

            muteBtn?.apply {
                visibility = if (config.addMute) View.VISIBLE else View.GONE
                setOnClickListener {

                    setMute(true)
                }

            }
            unMuteBtn?.apply {

                setOnClickListener {

                    setMute(false)
                }

            }

            lockBtn?.apply {
                Log.d(TAG, "lockBtn?.apply ")
                visibility = if (config.addLock) View.VISIBLE else View.GONE
                setOnClickListener {
                    updateLockStatus(true)
                }

            }

            unLockBtn?.apply {

                setOnClickListener {
                    updateLockStatus(false)
                }

            }
            backBtn?.apply {

                visibility = if (config.addLock) View.VISIBLE else View.GONE
                setOnClickListener {
                    onControlViewClick.onBackClick()
                }

            }

        }


    }

    private fun setMute(mute: Boolean) {
        exoPlayer?.volume = if (mute) 0f else 1f
        setMuteVisibility(mute)

    }

    private fun setMuteVisibility(isMute: Boolean) {

        muteBtn?.visibility = if (isMute) View.GONE else View.VISIBLE
        unMuteBtn?.visibility = if (isMute) View.VISIBLE else View.GONE
    }

    private fun updateLockStatus(isLock: Boolean) {

        Log.d(TAG, "updateLockStatus: $isLock ")
        isAllowToBack = !isLock
        backBtn?.visibility = if (isAllowToBack) View.VISIBLE else View.GONE
        lockBtn?.visibility = if (isLock) View.GONE else View.VISIBLE
        unLockBtn?.visibility = if (isLock) View.VISIBLE else View.GONE

        playerView?.apply {
            useController = !isLock
            showController()
        }


    }
}