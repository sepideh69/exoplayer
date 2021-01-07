package com.example.exoplayer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SubtitleEntity(
                          var title: String?,
                          var type: String?,
                          var source: String?,
                          var fileId: Int?,
                          var user: String?): Parcelable {


    var id: Int? = null
}