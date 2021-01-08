package com.example.exoplayer.presentation

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.R
import com.example.exoplayer.presentation.player.BasePlayerActivity
import kotlinx.android.synthetic.main.exo_player_control_view.view.*

class PlayerActivity : BasePlayerActivity() {

    private val TAG = "MY_PLAYER"

    //for test
    private var testBtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("khar", "onCreate: ")
        setContentView(R.layout.activity_player)
        initYaraPlayer()

//        initData()
    }

//    private fun initData() {
//
//        testBtn = getView()?.findViewById(R.id.btn_test)
//        testBtn?.setOnClickListener {
//            Log.d(TAG, "click on test btn ")
//        }
//    }
//
//    //for test
//    override fun onSettingClick() {
//        Log.d(TAG, "videoEnded: onSettingClick")
//        super.onSettingClick()
//    }
//
//
//    override fun videoEnded() {
//        Log.d(TAG, "videoEnded: PlayerActivity")
//        super.videoEnded()
//    }

}