package com.example.exoplayer.presentation.player

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.R
import com.example.exoplayer.data.VideoSource
import com.example.exoplayer.presentation.AppConstant.Companion.PLAYER_TAG
import com.example.exoplayer.presentation.player.config.ControlViewConfig
import com.example.exoplayer.presentation.player.config.MediaItemConfig
import com.example.exoplayer.presentation.util.CacheDataSourceFactory
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

abstract class BasePlayerActivity : AppCompatActivity(), PlayerCallBack, OnControlViewClick {


    var simpleExoPlayer: SimpleExoPlayer? = null
    private var alertDialog: AlertDialog? = null

    private var typeface: Typeface? = null
    private val defaultTrackSelector by lazy {
        DefaultTrackSelector(this)
    }
    private val adsLoader by lazy {
        ImaAdsLoader.Builder(this).build()
    }

    private var startIndex = 0
    private var startPosition: Long = 0

    var isShowingTrackSelectionDialog = false
    var playerWhenReady = true
    var currentWindow = 0
    var playbackPosition: Long = 0
    var allowedToBack = true

    var playerManager: PlayerManager? = null

    abstract fun getYaraPlayerView(): YaraPlayerView
    abstract fun getVideoSource(): VideoSource
    abstract fun getMediaItemConfig(): MediaItemConfig?
    abstract fun getControlViewConfig(): ControlViewConfig?

    open fun setSelectedIndex(selectedIndex: Int) {
        startIndex = selectedIndex
    }

    open fun setSelectedPosition(selectedPosition: Long) {
        startPosition = selectedPosition
    }

    open fun setSubtitleTypeFace(customTypeface: Typeface) {
        typeface = customTypeface
    }


    override fun onStart() {
        getYaraPlayerView().init()
        initData()
        super.onStart()
        if (Util.SDK_INT >= 24)
            initBasePlayer()

    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24)
            initBasePlayer()

    }

    private fun initData() {

        typeface = ResourcesCompat.getFont(this, R.font.iran_sans_font_family)

        defaultTrackSelector.setParameters(
            defaultTrackSelector.buildUponParameters()
                .setRendererDisabled(C.TRACK_TYPE_VIDEO, false)
        )

        configExoPlayer()

        getYaraPlayerView().exoPlayerView?.player = simpleExoPlayer
        playerManager = PlayerManager(
            getYaraPlayerView(),
            getVideoSource(),
            getMediaItemConfig() ?: MediaItemConfig(),
            this,
            getControlViewConfig(),
            this,
            startIndex,
            startPosition
        )
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
                .setAdViewProvider(getYaraPlayerView().exoPlayerView)



        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setTrackSelector(defaultTrackSelector)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        adsLoader.setPlayer(simpleExoPlayer)

        //add ads listener

    }


    private fun initBasePlayer() {

        playerManager?.start()

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
        getYaraPlayerView().backBtn?.visibility = if (isOpen) View.GONE else View.VISIBLE
        visibilityOfControlView(!isOpen)
        allowedToBack = !isOpen
        simpleExoPlayer?.apply {
            playWhenReady = !isOpen
        }


    }

    private fun visibilityOfSubtitle(isVisible: Boolean) {
        getYaraPlayerView().exoPlayerView?.subtitleView?.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    private fun visibilityOfControlView(isVisible: Boolean) {
        getYaraPlayerView().exoPlayerView?.apply {

            useController = isVisible

            if (isVisible) {
                showController()
            } else {
                hideController()
            }
        }


    }

    private fun openSettingDialog() {


        if (!isShowingTrackSelectionDialog &&
            SettingDialogFragment.willHaveContent(defaultTrackSelector)
        ) {


            var currentBitrate = 0
            simpleExoPlayer?.apply {
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

        simpleExoPlayer?.apply {
            playWhenReady = playerWhenReady
            playbackPosition = currentPosition
            currentWindow = currentWindowIndex
            release()
        }

    }


    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        getYaraPlayerView().exoPlayerView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


    //OnControlViewClick
    override fun onMuteClick() {
        setMute(true)
    }

    override fun onUnMuteClick() {
        setMute(false)
    }

    override fun onRewClick() {
        simpleExoPlayer?.apply {
            seekTo(currentPosition - 15000)
        }
    }

    override fun onSettingClick() {
        openSettingDialog()
    }

    override fun onSubtitleClick(subtitles: ArrayList<MediaItem.Subtitle>) {
        openSubtitleDialog(subtitles)
    }

    override fun onLockClick() {
        setLock(true)
    }

    override fun onUnLockClick() {
        setLock(false)
    }


    override fun onBackClick() {
        onBackPressed()
    }


    fun setLock(isLock: Boolean) {
        val isAllowToBack = !isLock
        with(getYaraPlayerView()) {

            backBtn?.visibility = if (isAllowToBack) View.VISIBLE else View.GONE
            lockBtn?.visibility = if (isLock) View.GONE else View.VISIBLE
            unLockBtn?.visibility = if (isLock) View.VISIBLE else View.GONE

            exoPlayerView?.apply {
                useController = !isLock
                showController()
            }
        }
    }

    //PlayerCallBack
    override fun onPlayerError(error: ExoPlaybackException) {
        TODO("Not yet implemented")
    }


    override fun onIdleState() {
        TODO("Not yet implemented")
    }

    override fun onBufferingState() {
        Log.d(PLAYER_TAG, "onBufferingState: ")
    }

    override fun onReadyState() {
        updateUi(playerManager?.getCurrentMediaItem())
    }

    override fun onEndedState() {
        Log.d(PLAYER_TAG, "onEndedState: ")
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        updateUi(mediaItem)
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        Log.d(PLAYER_TAG, "onTimelineChanged: ")
    }


    private fun updateUi(currentMediaItem: MediaItem?) {
        currentMediaItem ?: return
        getYaraPlayerView().videoTitle?.text =
            currentMediaItem.playbackProperties?.tag.toString()

        playerManager ?: return
        setSubtitleImage(playerManager!!.hasSubtitle())

    }

    private fun setMute(mute: Boolean) {
        simpleExoPlayer?.volume = if (mute) 0f else 1f
        setMuteVisibility(mute)

    }

    private fun setMuteVisibility(isMute: Boolean) {
        with(getYaraPlayerView()) {
            muteBtn?.visibility = if (isMute) View.GONE else View.VISIBLE
            unMuteBtn?.visibility =
                if (isMute) View.VISIBLE else android.view.View.GONE
        }
    }

    private fun setSubtitleImage(hasSubtitle: Boolean = false) {
        getYaraPlayerView().subtitleBtn?.apply {
            if (hasSubtitle)
                setImageResource(R.drawable.exo_subtitle_btn)
            else
                setImageResource(R.drawable.exo_no_subtitle_btn)

        }

    }


}

