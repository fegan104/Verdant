package com.frankegan.verdant.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by frankegan on 3/4/18.
 */
@Parcelize
@Entity(tableName = "image")
data class ImgurImage(
        @PrimaryKey
        val id: String,
        val title: String,
        val description: String?,
        val animated: Boolean = false,
        val views: Int,
        var favorite: Boolean = false
) : Parcelable {

    val link: String
        get() = "https://i.imgur.com/$id.jpg"

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

data class ApiResponse<T>(val success: Boolean, val status: Int, val data: T)

sealed class Result<out T : Any> {

    class Success<out T : Any>(val data: T) : Result<T>()

    class Error(val exception: Throwable) : Result<Nothing>()
}

open class DataSourceException(message: String? = null) : Exception(message)

class RemoteDataNotFoundException : DataSourceException("Data not found in remote data source")

class LocalDataNotFoundException : DataSourceException("Data not found in local data source")