package com.example.exoplayer.presentation

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.R
import com.example.exoplayer.data.VideoSource
import com.example.exoplayer.presentation.player.BasePlayerActivity
import com.example.exoplayer.presentation.player.YaraPlayerView
import com.example.exoplayer.presentation.player.config.ControlViewConfig
import com.example.exoplayer.presentation.player.config.MediaItemConfig
import com.example.exoplayer.presentation.util.IntentUtil
import com.example.exoplayer.presentation.util.TestProvider
import kotlinx.android.synthetic.main.exo_player_control_view.view.*

class PlayerActivity : BasePlayerActivity() {

    private val TAG = "MY_PLAYER"
    private lateinit var yaraPlayerView: YaraPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("fgdfgd", "onCreate: ")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initData()
    }


    private fun initData() {
        Log.d("fgdfgd", "initData: ")
        yaraPlayerView = findViewById(R.id.yara_player_view)
    }

    override fun getYaraPlayerView(): YaraPlayerView {
        Log.d("fgdfgd", "getYaraPlayerView: ")
        return yaraPlayerView
    }

    override fun getVideoSource(): VideoSource {
        return intent.getParcelableExtra(IntentUtil.VIDEO_SOURCE_EXTRA)
    }

    override fun getMediaItemConfig(): MediaItemConfig? {
        return intent.getParcelableExtra(IntentUtil.MEDIA_ITEM_CONFIG_EXTRA)
    }

    override fun getControlViewConfig(): ControlViewConfig? {
        return intent.getParcelableExtra(IntentUtil.CONTROL_VIEW_CONFIG_EXTRA)
    }

   }


