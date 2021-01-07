package com.example.exoplayer.presentation.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.R
import com.example.exoplayer.data.VideoSource
import com.example.exoplayer.presentation.AppConstant
import com.example.exoplayer.presentation.player.config.ControlViewConfig
import com.example.exoplayer.presentation.player.config.MediaItemConfig
import com.example.exoplayer.presentation.util.CacheDataSourceFactory
import com.example.exoplayer.presentation.util.IntentUtil
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util

open class BasePlayerActivity : AppCompatActivity(), PlayerCallBack, OnControlViewClick {

    private var TAG = "my_player"

    private var view: View? = null
    private var playerView: PlayerView? = null
    private var backBtn: ImageButton? = null

    private var exoPlayer: SimpleExoPlayer? = null
    private var videoSource: VideoSource? = null
    private var alertDialog: AlertDialog? = null


    private val typeface by lazy {
        ResourcesCompat.getFont(this, R.font.iran_sans_font_family)
    }

    private val defaultTrackSelector by lazy {
        DefaultTrackSelector(this)
    }
    private val adsLoader by lazy {
        ImaAdsLoader.Builder(this).build()
    }

    private var mediaItemConfig: MediaItemConfig? = null
    private var controlViewConfig: ControlViewConfig? = null

    var isShowingTrackSelectionDialog = false
    var playerWhenReady = true
    var currentWindow = 0
    var playbackPosition: Long = 0
    var allowedToBack = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_base_player)
        getDataFromIntent()
        initData()
    }

    private fun initData() {
        view = findViewById(R.id.player_view_parent)
        playerView = findViewById(R.id.demo_player_view)
        backBtn = findViewById(R.id.btn_back)


        defaultTrackSelector.setParameters(
            defaultTrackSelector.buildUponParameters()
                .setRendererDisabled(C.TRACK_TYPE_VIDEO, false)
        )

        configExoPlayer()

        playerView?.player = exoPlayer


    }


    private fun getDataFromIntent() {

        Log.d(TAG, "getDataFromIntent: ")
        intent?.let { intent ->

            videoSource = intent.getParcelableExtra(IntentUtil.VIDEO_SOURCE_EXTRA)
            mediaItemConfig = intent.getParcelableExtra(IntentUtil.MEDIA_ITEM_CONFIG_EXTRA)
            controlViewConfig = intent.getParcelableExtra(IntentUtil.CONTROL_VIEW_CONFIG_EXTRA)


        }


    }

    private fun configExoPlayer() {

        val cacheFactory = CacheDataSourceFactory(
            this,
            100 * 1024 * 1024,
            5 * 1024 * 1024
        )

        val mediaSourceFactory =
            DefaultMediaSourceFactory(cacheFactory)
                .setAdsLoaderProvider { adsLoader }
                .setAdViewProvider(playerView)



        exoPlayer = SimpleExoPlayer.Builder(this)
            .setTrackSelector(defaultTrackSelector)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        adsLoader.setPlayer(exoPlayer)

        //add ads listener

    }


    private fun initializePlayer() {

        Log.d(TAG, "initializePlayer")
        Log.d(TAG, "controlViewConfig: $controlViewConfig")

        BasePlayer(
            view,
            exoPlayer,
            videoSource,
            mediaItemConfig ?: MediaItemConfig(),
            this,
            controlViewConfig,
            this
        ).start()

    }


    private fun openSubtitleDialog(subtitles: ArrayList<MediaItem.Subtitle>) {

        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.subtitle_selection_dialog, null)

        val title: TextView = view.findViewById(R.id.subtitle_dialog_header)
        val cancel: TextView = view.findViewById(R.id.cancel_dialog_btn)
        val noSub: TextView = view.findViewById(R.id.no_subtitle_text_view)
        val recyclerView: RecyclerView = view.findViewById(R.id.subtitle_recycler_view)
        val subtitleAdapter = SubtitleAdapter {

            dialogIsOpen(false)
            visibilityOfSubtitle(true)
            //update subtitle

        }
        recyclerView.adapter = subtitleAdapter
        subtitleAdapter.submitList(subtitles)

        typeface?.let { typeFace ->
            title.typeface = typeFace
            cancel.typeface = typeFace
            noSub.typeface = typeFace

        }

        cancel.setOnClickListener {

            dialogIsOpen(false)
        }
        noSub.setOnClickListener {

            visibilityOfSubtitle(false)
            dialogIsOpen(false)
        }


        builder.setView(view)
        alertDialog = builder.create()


        // set the height and width of dialog
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog!!.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER

        alertDialog?.apply {
            window?.attributes = layoutParams
            setCanceledOnTouchOutside(false);
            show()
        }

        dialogIsOpen(true)


    }

    private fun dialogIsOpen(isOpen: Boolean) {

        if (!isOpen)
            alertDialog?.dismiss()
        backBtn?.visibility = if (isOpen) View.GONE else View.VISIBLE
        visibilityOfControlView(!isOpen)
        allowedToBack = !isOpen
        exoPlayer?.apply {
            playWhenReady = !isOpen
        }


    }

    private fun visibilityOfSubtitle(isVisible: Boolean) {
        playerView?.subtitleView?.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun visibilityOfControlView(isVisible: Boolean) {
        playerView?.apply {

            useController = isVisible

            if (isVisible) {
                showController()
            } else {
                hideController()
            }
        }


    }

    private fun openSettingDialog() {

        Log.d(TAG, "openSettingDialog: ")

        if (!isShowingTrackSelectionDialog &&
            SettingDialogFragment.willHaveContent(defaultTrackSelector)
        ) {


            var currentBitrate = 0
            exoPlayer?.apply {
                playWhenReady = false
                videoFormat?.let {
                    currentBitrate = it.bitrate
                }
                val qualityDialogFragment =
                    SettingDialogFragment(defaultTrackSelector,
                        0,
                        currentBitrate,
                        DialogInterface.OnDismissListener { isShowingTrackSelectionDialog = false })
                qualityDialogFragment.show(supportFragmentManager, null)
            }


        }
    }

    fun getView(): View? {
        return view
    }

    override fun onStart() {

        super.onStart()
        if (Util.SDK_INT >= 24)
            initializePlayer()

    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24)
            initializePlayer()


    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24)
            releasePlayer()

    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24)
            releasePlayer()
    }

    private fun releasePlayer() {

        exoPlayer?.apply {
            playWhenReady = playerWhenReady
            playbackPosition = currentPosition
            currentWindow = currentWindowIndex
            release()
        }

    }


    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun videoEnded() {
        Log.d(TAG, "videoEnded: BasePlayerActivity")
    }

    override fun onRewClick() {
        exoPlayer?.apply {
            seekTo(currentPosition - 15000)
        }
    }

    override fun onSettingClick() {
        openSettingDialog()
    }

    override fun onSubtitleClick(subtitles: ArrayList<MediaItem.Subtitle>) {
        openSubtitleDialog(subtitles)
    }

    override fun onBackClick() {
        onBackPressed()
    }


}

