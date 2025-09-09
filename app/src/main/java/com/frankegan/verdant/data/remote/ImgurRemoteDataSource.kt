package com.frankegan.verdant.data.remote

import android.util.Log
import com.frankegan.verdant.data.ImgurDataSource
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.data.ImgurUser
import com.frankegan.verdant.data.RemoteDataNotFoundException


class ImgurRemoteDataSource private constructor(
    val apiService: ImgurApiService
) : ImgurDataSource {
    override suspend fun getImage(id: String): Result<ImgurImage> {
        val response = apiService.getImage(id).await()
        return if (response.success) {
            Result.success(response.data)
        } else {
            Result.failure(RemoteDataNotFoundException())
        }
    }

    override suspend fun getImages(subreddit: String, page: Int): Result<List<ImgurImage>> {
        val response = apiService.listImages(subreddit, page).await()
        Log.d("ImgurRemoteDataSource", response.toString())
        return if (response.success) {
            Result.success(response.data)
        } else {
            Result.failure(RemoteDataNotFoundException())
        }
    }

    override suspend fun favoriteImage(image: ImgurImage): Result<String> {
        return try {
            val response = apiService.toggleFavoriteImage(image.id).await()
            Result.success(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(RemoteDataNotFoundException())
        }
    }

    override suspend fun deleteImages() {
        //not relevent to remove from server
    }

    override suspend fun saveImages(images: List<ImgurImage>) {
        //we don't have this feature
    }

    override suspend fun getUsername(): Result<String> = Result.failure(RemoteDataNotFoundException())

    override suspend fun saveUser(user: ImgurUser) {
        //we don't support this
    }

    override suspend fun refreshImages() {
        // Not required because the {@link ImgurRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    companion object {
        private var INSTANCE: ImgurRemoteDataSource? = null

        @JvmStatic
        fun getInstance(token: String): ImgurRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(ImgurRemoteDataSource::javaClass) {

                    INSTANCE = ImgurRemoteDataSource(
                        apiService = ImgurApiService.create(token)
                    )
                }
            }
            return INSTANCE!!
        }

        internal fun clearInstance() {
            INSTANCE = null
        }
    }
}