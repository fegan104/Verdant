package com.frankegan.verdant.models

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * Created by frankegan on 3/4/18.
 */
@Parcelize
data class ImgurImage (
        val id: String,
        val title: String,
        val description: String,
        val animated: Boolean = false,
        val views: Int
) : Parcelable {
    @IgnoredOnParcel
    val thumbLink: String = "https://i.imgur.com/$id.jpg"
    @IgnoredOnParcel
    val medThumbLink: String = "https://i.imgur.com/${id}m.jpg"
    @IgnoredOnParcel
    val bigThumbLink: String = "https://i.imgur.com/${id}h.jpg"
}

data class ImgurUser(
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Long,
        val accountUsername: String
)