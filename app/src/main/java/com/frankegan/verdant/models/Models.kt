package com.frankegan.verdant.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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
        val description: String?,
        val animated: Boolean = false,
        val views: Int,
        val favorite: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    val link: String = "https://i.imgur.com/$id.jpg"

    val medThumbLink: String
        get() = "https://i.imgur.com/${id}m.jpg"

    val bigThumbLink: String
        get() = "https://i.imgur.com/${id}h.jpg"
}

@Entity(tableName = "user")
data class ImgurUser(
        @PrimaryKey
        val username: String,
        @ColumnInfo(name = "access_token")
        val accessToken: String,
        @ColumnInfo(name = "refresh_token")
        val refreshToken: String,
        @ColumnInfo(name = "expires_at")
        val expiresAt: Long
)