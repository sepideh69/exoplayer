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
    private val yaraPlayerView: YaraPlayerView,
    private val onControlViewClick: OnControlViewClick
) {

    private var TAG = "my_player"
    private var config: ControlViewConfig? = null


    var subtitleList = ArrayList<MediaItem.Subtitle>()


    fun start(controlViewConfig: ControlViewConfig) {

        config = controlViewConfig
        customiseController()
    }


    private fun customiseController() {

        Log.d(TAG, "handleCustomControlView: $config")

        config ?: return

        with(yaraPlayerView) {

            settingBtn?.apply {
                visibility = if (config!!.adSetting) View.VISIBLE else View.GONE
                setOnClickListener {

                    onControlViewClick.onSettingClick()
                }

            }
            subtitleBtn?.apply {
                visibility = if (config!!.adSubtitle) View.VISIBLE else View.GONE
                setOnClickListener {

                    onControlViewClick.onSubtitleClick(subtitleList)
                }

            }

            muteBtn?.apply {
                visibility = if (config!!.addMute) View.VISIBLE else View.GONE
                setOnClickListener {
                    onControlViewClick.onMuteClick()

                }

            }
            unMuteBtn?.apply {

                setOnClickListener {
                    onControlViewClick.onUnMuteClick()

                }

            }

            lockBtn?.apply {
                Log.d(TAG, "lockBtn?.apply ")
                visibility = if (config!!.addLock) View.VISIBLE else View.GONE
                setOnClickListener {
                    onControlViewClick.onLockClick(true)
                }


            }


        }

        yaraPlayerView.unLockBtn?.apply {

            setOnClickListener {
                onControlViewClick.onLockClick(false)
            }

        }
        yaraPlayerView.backBtn?.apply {

            visibility = if (config!!.addLock) View.VISIBLE else View.GONE
            setOnClickListener {
                onControlViewClick.onBackClick()
            }

        }


    }

    fun updateSubtitleImage(hasSubtitle: Boolean) {
        yaraPlayerView.subtitleBtn?.apply {
            if (hasSubtitle)
                setImageResource(R.drawable.exo_subtitle_btn)
            else
                setImageResource(R.drawable.exo_no_subtitle_btn)

        }

    }


}