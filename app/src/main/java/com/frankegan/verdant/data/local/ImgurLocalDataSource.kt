package com.frankegan.verdant.data.local

import com.frankegan.verdant.data.*

class ImgurLocalDataSource private constructor(
    private val database: VerdantDatabase = VerdantDatabase.getInstance()
) : ImgurDataSource {
    override suspend fun getImage(id: String): Result<ImgurImage> {
        val response = database.imageDao().getImage(id)
        return if (response != null) {
            Result.success(response)
        } else {
            Result.failure(LocalDataNotFoundException())
        }
    }

    override suspend fun favoriteImage(image: ImgurImage): Result<String> {
        database.imageDao().updateFavorited(image.id, image.favorite)
        return if (image.favorite) {
            Result.success("favorite")
        } else {
            Result.failure(Error("unfavorite"))
        }
    }

    override suspend fun getUsername(): Result<String> {
        return Result.success(database.userDao().getUsername())
    }

    override suspend fun saveUser(user: ImgurUser) {
        return database.userDao().insertAll(user)
    }

    /**
     * Not required because the [ImgurRepository] handles the logic of
     * refreshing the tasks from all the available data sources.
     */
    override suspend fun refreshImages() = Unit

    override suspend fun deleteImages() {
        database.imageDao().deleteImages()
    }

    override suspend fun saveImages(images: List<ImgurImage>) {
        database.imageDao().insertAll(images = images.toTypedArray())
    }

    companion object {
        private var INSTANCE: ImgurLocalDataSource? = null

        @JvmStatic
        fun getInstance(): ImgurLocalDataSource {
            if (INSTANCE == null) {
                synchronized(ImgurLocalDataSource::javaClass) {
                    INSTANCE = ImgurLocalDataSource()
                }
            }
            return INSTANCE!!
        }
    }
}