package com.frankegan.verdant.data.remote

import android.support.annotation.VisibleForTesting
import android.util.Log
import com.frankegan.verdant.data.*
import com.frankegan.verdant.utils.AppExecutors
import kotlinx.coroutines.experimental.withContext

class ImgurRemoteDataSource private constructor(
        val appExecutors: AppExecutors = AppExecutors(),
        val apiService: ImgurApiService
) : ImgurDataSource {
    override suspend fun getImage(id: String): Result<ImgurImage> =
            withContext(appExecutors.networkContext) {
                val response = apiService.getImage(id).await()
                if (response.success) {
                    Result.Success(response.data)
                } else {
                    Result.Error(RemoteDataNotFoundException())
                }
            }

    override suspend fun getImages(subreddit: String, page: Int): Result<List<ImgurImage>> =
            withContext(appExecutors.networkContext) {
                val response = apiService.listImages(subreddit, page).await()
                Log.d("ImgurRemoteDataSource", response.toString())
                if (response.success) {
                    Result.Success(response.data)
                } else {
                    Result.Error(RemoteDataNotFoundException())
                }
            }

    override suspend fun favoriteImage(image: ImgurImage): Result<String> =
            withContext(appExecutors.networkContext) {
                return@withContext try {
                    val response = apiService.toggleFavoriteImage(image.id).await()
                    Result.Success(response.data)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(RemoteDataNotFoundException())
                }
            }

    override suspend fun deleteImages() {
        //not relevent to remove from server
    }

    override suspend fun saveImages(images: List<ImgurImage>) {
        //we don't have this feature
    }

    override suspend fun getUsername(): Result<String> = Result.Error(RemoteDataNotFoundException())

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

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}