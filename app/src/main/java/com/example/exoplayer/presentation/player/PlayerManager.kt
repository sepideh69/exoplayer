package com.example.exoplayer.presentation.player

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.exoplayer.R
import com.example.exoplayer.data.SubtitleEntity
import com.example.exoplayer.data.VideoSource
import com.example.exoplayer.presentation.AppConstant.Companion.PLAYER_TAG
import com.example.exoplayer.presentation.player.config.ControlViewConfig
import com.example.exoplayer.presentation.player.config.MediaItemConfig
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.MimeTypes


class PlayerManager(
    private var yaraPlayerView: YaraPlayerView,
    private var videoSource: VideoSource,
    private var mediaItemConfig: MediaItemConfig,
    private var playerCallBack: PlayerCallBack,
    var controlViewConfig: ControlViewConfig?,
    private var onControlViewClick: OnControlViewClick,
    var selectedIndex: Int,
    var selectedPosition: Long

) {

    private var mediaItems = arrayListOf<MediaItem>()

    private val subtitleList = ArrayList<MediaItem.Subtitle>()
    private var customControlView =
        CustomControlView(
            yaraPlayerView,
            onControlViewClick
        )


    private var videoTitle: TextView? = null
    private var exoRew: ImageButton? = null


    init {

        setUpLayout()
    }

    private fun setUpLayout() {
        Log.d(PLAYER_TAG, "setUpLayout: base")

        yaraPlayerView.exoPlayerView?.apply {
            videoTitle = findViewById(R.id.exo_Video_title)
            exoRew = findViewById(R.id.exo_rew)
            exoRew?.setOnClickListener {
                onControlViewClick.onRewClick()
            }


        }

        controlViewConfig?.let {
            customControlView.start(it)
        }


    }

    fun start() {

        Log.d(PLAYER_TAG, "start: base")

//        videoSource.let { videoSource ->
//           selectedIndex = videoSource.selectedSourceIndex
//        }
        makeMediaItem()

    }


    private fun makeMediaItem() {

        Log.d(PLAYER_TAG, "makeMediaItem: $mediaItemConfig")

        videoSource.video?.let { list ->

            for (video in list) {

//                if (list.indexOf(video) == selectedIndex)
//                    selectedPosition = video.watchedLength

                mediaItems.add(
                    MediaItem.Builder()
                        .setMediaId(video.id.toString())
                        .setUri(Uri.parse(video.videoUrl))
                        .let {
                            if (mediaItemConfig.initAds) {
                                it.setAdTagUri(Uri.parse(video.adsUrl))
                            }
                            it
                        }
                        .let {
                            if (mediaItemConfig.addSubtitle)
                                it.setSubtitles(makeSubtitleList(video.subtitles))
                            it
                        }
                        .setTag(video.title)
                        .build()
                )

            }
            initPlayer()
        }


    }


    private fun makeSubtitleList(
        subtitles: List<SubtitleEntity>?
    ): List<MediaItem.Subtitle> {

        if (!subtitles.isNullOrEmpty()) {

            for (item in subtitles) {
                val subtitle = MediaItem.Subtitle(
                    Uri.parse(item.source),
                    if (item.type.equals(".vtt")) MimeTypes.TEXT_VTT else MimeTypes.APPLICATION_SUBRIP,
                    item.title,
                    C.SELECTION_FLAG_DEFAULT
                )

                subtitleList.add(subtitle)

                Log.d(PLAYER_TAG, "createSubtitleList: for  ${subtitleList.size}")
            }

            customControlView.subtitleList = subtitleList
        }


        return subtitleList

    }


    private fun initPlayer() {


        yaraPlayerView.exoPlayerView?.player?.apply {
            resetPlayer()
            setMediaItems(mediaItems, selectedIndex, selectedPosition)
            prepare()
            play()
            addListener(PlayerEventListener())
        }

    }


    private fun resetPlayer() {

        val haveStartPosition: Boolean = selectedPosition.compareTo(0) != 0
        val haveStartIndex = selectedIndex != 0
        if (haveStartPosition || haveStartIndex) {
            yaraPlayerView.exoPlayerView?.player?.seekTo(selectedIndex, selectedPosition)
        }

    }

    inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerError(error: ExoPlaybackException) {
            playerCallBack.onPlayerError(error)
            super.onPlayerError(error)
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            when (state) {
                Player.STATE_IDLE ->playerCallBack.onIdleState()
                Player.STATE_BUFFERING -> playerCallBack.onBufferingState()
                Player.STATE_READY -> playerCallBack.onReadyState()
                Player.STATE_ENDED -> playerCallBack.onEndedState()


            }
        }


        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {

            playerCallBack.onMediaItemTransition(mediaItem, reason)

        }


        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            playerCallBack.onTimelineChanged(timeline, reason)

        }

    }


    fun hasSubtitle(): Boolean {

        val currentId = getCurrentMediaItem()?.mediaId
        videoSource.video?.let { videos ->
            if (currentId == null)
                return@let
            for (video in videos) {
                if (video.id == currentId && !video.subtitles.isNullOrEmpty()) {
                    Log.d(PLAYER_TAG, "checkSubtitle: true")
                    return true
                }

            }
        }

        return false
    }


    fun getCurrentMediaItem(): MediaItem? {
        return yaraPlayerView.exoPlayerView?.player?.currentMediaItem
    }

}

