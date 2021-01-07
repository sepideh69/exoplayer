package com.example.exoplayer.presentation.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.exoplayer.R
import com.example.exoplayer.data.SubtitleEntity
import com.example.exoplayer.data.VideoSource
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes


class TestProvider(val context: Context) {

    private var TAG = "my_player"


    fun makeTestVideoSource(): VideoSource {


        val singleVideos = arrayListOf<VideoSource.SingleVideo>()
        singleVideos.add(
            VideoSource.SingleVideo(
                id = 1.toString(),
                videoType = C.TYPE_OTHER,
                videoUrl = context.getString(R.string.video_five),
                adsUrl = context.getString(R.string.ads_url),
                title = "ویدیو اول",
                watchedLength = 0,
                isValidToShowAd = true,
                subtitles = addTestSubtitleToList(
                    ".vtt",
                    context.getString(R.string.subtitle_five)
                )
            )
        )

//        singleVideos.add(
//            VideoSource.SingleVideo(
//                id = 2.toString(),
//                videoType = C.TYPE_OTHER,
//                videoUrl = context.getString(R.string.video_two),
//                title = "ویدیو دوم",
//                watchedLength = 30000,
//                isValidToShowAd = true
//            )
//        )
//        singleVideos.add(
//            VideoSource.SingleVideo(
//                id = 3.toString(),
//                videoType = C.TYPE_OTHER,
//                videoUrl = context.getString(R.string.video_three),
//                title = "ویدیو سوم",
//                watchedLength = 0,
//                isValidToShowAd = true
//            )
//        )


        return VideoSource(singleVideos)
    }

    fun makeHlsTestVideoSourceList(): VideoSource {

        Log.d(TAG, "makeHlsTestVideoSourceList: ")
        val singleVideos = arrayListOf<VideoSource.SingleVideo>()
        singleVideos.add(
            VideoSource.SingleVideo(
                id = 1.toString(),
                videoType = C.TYPE_OTHER,
                videoUrl = context.getString(R.string.toy_story_url),
                adsUrl = context.getString(R.string.ads_url),
                title = "داستان اسباب بازی ها",
                watchedLength = 0,
                isValidToShowAd = true,
                subtitles = addTestSubtitleToList(
                    ".srt",
                    context.getString(R.string.subtitle_toy_story)
                )
            )
        )

//        singleVideos.add(
//            VideoSource.SingleVideo(
//                id = 2.toString(),
//                videoType = C.TYPE_OTHER,
//                videoUrl = context.getString(R.string.video_two),
//                title = "ویدیو دوم",
//                watchedLength = 0,
//                isValidToShowAd = true
//            )
//        )
//        singleVideos.add(
//            VideoSource.SingleVideo(
//                id = 3.toString(),
//                videoType = C.TYPE_OTHER,
//                videoUrl = context.getString(R.string.video_three),
//                title = "ویدیو سوم",
//                watchedLength = 0,
//                isValidToShowAd = true
//            )
//        )


        return VideoSource(singleVideos, 0)
    }


    private fun addTestSubtitleToList(mimeTypes: String, uri: String): ArrayList<SubtitleEntity> {

        val subtitleList = ArrayList<SubtitleEntity>()
        val subtitle = SubtitleEntity(
            "زیرنویس تست",
            mimeTypes,
            uri,
            1,
            null
        )
        subtitleList.add(subtitle)
        val subtitle2 = SubtitleEntity(
            " زیرنویس تست دوم",
            mimeTypes,
            context.getString(R.string.subtitle_toy_story),
            1,
            null
        )

        subtitleList.add(subtitle2)

        Log.d(TAG, "makeFakeSubtitle: ${subtitleList.size}")
        return subtitleList


    }

//    fun addTestSubtitleToList(): MutableList<MediaItem.Subtitle> {
//
//
//        val subtitleList = mutableListOf<MediaItem.Subtitle>()
//        val subtitle = MediaItem.Subtitle(
//            Uri.parse(context.getString(R.string.subtitle_five)),
//            MimeTypes.TEXT_VTT,
//            "eng",
//            C.SELECTION_FLAG_DEFAULT
//        )
//
//        subtitleList.add(subtitle)
//
//        Log.d(TAG, "makeFakeSubtitle: ${subtitleList.size}")
//        return subtitleList
//
//
//    }
}