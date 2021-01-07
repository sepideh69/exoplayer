package com.example.exoplayer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class VideoSource(var video: List<SingleVideo>? = null,
                       var selectedSourceIndex: Int = 0

) : Parcelable {


    @Parcelize
    data class SingleVideo(var id: String? = null,
                           var fileType: Int? = null,
                           var videoType: Int? = 0,
                           var videoUrl: String? = null,
                           var adsUrl : String  ="",
                           var title: String? = null,
                           var subtitles: ArrayList<SubtitleEntity>? = null,
                           var watchedLength: Long = 0L,
                           var isPreview: Boolean = false,
                           var isDownloaded : Boolean = false,
                           var nextEpisodeImage: String? = null,
                           var nextEpisodeTitle: String? = null,
                           var hashKey: String ?= null,
                           var isValidToShowAd: Boolean = false,
                           var isWatchedLengthChanged: Boolean = false //will be true if video is watched in player
    ) : Parcelable {


        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val that = o as SingleVideo?
            if (id != that!!.id) return false
            return if (title != null) title == that.title else that.title == null
        }

        override fun hashCode(): Int {
            var result = id!!.hashCode()
            result = 31 * result + if (title != null) title!!.hashCode() else 0
            return result
        }

        fun makeNextEpisodeText(): String {
            return if (nextEpisodeTitle != null)
                "قسمت بعدی $nextEpisodeTitle"
            else "قسمت بعدی"
        }
    }

}