package com.frankegan.verdant.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by frankegan on 3/4/18.
 */
@Parcelize
data class ImgurImage (
        val id: String,
        val title: String,
        val description: String,
        val animated: Boolean,
        val views: Int
) : Parcelable {
    val thumbLink: String = "https://i.imgur.com/$id.jpg"
    val medThumbLink: String = "https://i.imgur.com/${id}m.jpg"
    val bigThumbLink: String = "https://i.imgur.com/${id}h.jpg"
}

data class ImgurUser(
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Long,
        val accountUsername: String
)