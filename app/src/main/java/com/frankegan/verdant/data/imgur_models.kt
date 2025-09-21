package com.frankegan.verdant.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by frankegan on 3/4/18.
 */
@Parcelize
@Serializable
@Entity(tableName = "image")
data class ImgurImage(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val animated: Boolean = false,
    val views: Int,
    val favorite: Boolean = false,
    val datetime: Long,
) : Parcelable {

    val extension: String
        get() = if (animated) "gif" else "jpeg"

    val link: String
        get() = "https://i.imgur.com/$id.$extension"

    val medThumbLink: String
        get() = "https://i.imgur.com/${id}m.jpg"

    val bigThumbLink: String
        get() = "https://i.imgur.com/${id}h.jpg"
}

@Entity(tableName = "user")
data class ImgurUser(
    @PrimaryKey
    val username: String,
    @ColumnInfo(name = "refresh_token")
    val refreshToken: String,
    @ColumnInfo(name = "expires_at")
    val expiresAt: Long
)

@Entity(tableName = "paging_key")
data class ImagePagingKey(
    @PrimaryKey val imageId: String,
    val prevKey: Int?,
    val nextKey: Int?
)

data class ApiResponse<T>(val success: Boolean, val status: Int, val data: T)

open class DataSourceException(message: String? = null) : Exception(message)

class RemoteDataNotFoundException : DataSourceException("Data not found in remote data source")

class LocalDataNotFoundException : DataSourceException("Data not found in local data source")