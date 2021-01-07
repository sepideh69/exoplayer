package com.example.exoplayer.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.exoplayer.R
import com.example.exoplayer.presentation.player.config.ControlViewConfig
import com.example.exoplayer.presentation.player.config.MediaItemConfig
import com.example.exoplayer.presentation.util.TestProvider
import com.example.exoplayer.presentation.util.IntentUtil

class MainActivity : AppCompatActivity() {

    private var basePlayer: TextView? = null
    private var subtitleWithCustomControl: TextView? = null
    private var srtAndHlsWithCustom: TextView? = null
    private val intentUtil by lazy {
        IntentUtil()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpLayout()

        basePlayer?.setOnClickListener {

            val intent = Intent(this, PlayerActivity::class.java)

            intentUtil.addToIntent(
                intent,
                TestProvider(this).makeTestVideoSource()
            )
            startActivity(intent)
        }

        subtitleWithCustomControl?.setOnClickListener {

            val intent = Intent(this, PlayerActivity::class.java)

            intentUtil.addToIntent(
                intent,
                TestProvider(this).makeTestVideoSource(),
                MediaItemConfig(initAds = true, addSubtitle = true),
                ControlViewConfig(
                    adSetting = true,
                    adSubtitle = true,
                    addLock = true,
                    addMute = false
                )
            )

            startActivity(intent)
        }

        srtAndHlsWithCustom?.setOnClickListener {

            val intent = Intent(this, PlayerActivity::class.java)
            intentUtil.addToIntent(
                intent,
                TestProvider(this).makeHlsTestVideoSourceList(),
                MediaItemConfig(initAds = false, addSubtitle = true),
                ControlViewConfig(
                    adSetting = false,
                    adSubtitle = true,
                    addLock = false,
                    addMute = true
                )
            )
            startActivity(intent)
        }

    }


    private fun setUpLayout() {
        basePlayer = findViewById<TextView>(R.id.base_player)
        subtitleWithCustomControl = findViewById<TextView>(R.id.subtitleWithCustomControl)
        srtAndHlsWithCustom = findViewById<TextView>(R.id.srtAndHlsWithCustom)
    }


}