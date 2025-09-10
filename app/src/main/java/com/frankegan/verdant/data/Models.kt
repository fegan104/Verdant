package com.frankegan.verdant.data

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
    val favorite: Boolean = false
) : Parcelable {

    private val extension: String
        get() = if (animated) "gif" else "jpeg"

    val link: String
        get() = "https://i.imgur.com/$id.$extension"

    val medThumbLink: String
        get() = "https://i.imgur.com/${id}m.jpg"

    val bigThumbLink: String
        get() = "https://i.imgur.com/${id}h.jpg"

    object NavType : androidx.navigation.NavType<ImgurImage>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): ImgurImage? {
            return bundle.getString(key)?.let { Json.decodeFromString(it) }
        }

        override fun parseValue(value: String): ImgurImage {
            return Json.decodeFromString(value)
        }

        override fun put(bundle: Bundle, key: String, value: ImgurImage) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
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