package com.frankegan.verdant.data

import com.frankegan.verdant.data.local.ImgurLocalDataSource
import com.frankegan.verdant.data.remote.ImgurRemoteDataSource
import java.util.*

class ImgurRepository private constructor(
        val remoteDataSource: ImgurDataSource,
        val localDataSource: ImgurDataSource
) : ImgurDataSource {

    /**
     * The in memory cache of our imgur data.
     * This variable has public visibility so it can be accessed from tests.
     */
    val cachedImages = LinkedHashMap<String, ImgurImage>()
    /**
     * The in memory cache for users.
     */
    var cachedUsers: ImgurUser? = null
    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var cacheIsDirty = false

    override suspend fun getImage(id: String): Result<ImgurImage> {
        val cachedImg = cachedImages[id]

        // Respond immediately with cache if available
        if (cachedImg != null) {
            return Result.Success(cachedImg)
        }
        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        val localResult = localDataSource.getImage(id)
        return when (localResult) {
            is Result.Success -> Result.Success(cache(localResult.data))
            is Result.Error -> {
                val remoteResult = remoteDataSource.getImage(id)
                when (remoteResult) {
                    is Result.Success -> Result.Success(cache(remoteResult.data))
                    is Result.Error -> Result.Error(RemoteDataNotFoundException())
                }
            }
        }
    }

    override suspend fun getImages(subreddit: String, page: Int): Result<List<ImgurImage>> {
        // Respond immediately with cache if available and not dirty
        if (cachedImages.isNotEmpty() && !cacheIsDirty) {
            return Result.Success(cachedImages.values.toList())
        }

        return if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getFromRemoteDataSource(subreddit, page)
        } else {
            // Query the local storage if available. If not, query the network.
            val result = localDataSource.getImages(subreddit, page)
            when (result) {
                is Result.Success -> {
                    refreshCache(result.data)
                    Result.Success(cachedImages.values.toList())
                }
                is Result.Error -> getFromRemoteDataSource(subreddit, page)
            }
        }
    }

    override suspend fun favoriteImage(image: ImgurImage): Result<String> {
        // Do in memory cache update to keep the app UI up to date
        image.favorite = !image.favorite
        return cache(image).let {
            remoteDataSource.favoriteImage(it)
            localDataSource.favoriteImage(it)
        }
    }

    override suspend fun getUsername(): Result<String> {
        val user = cachedUsers
        return if (user != null) Result.Success(user.username) else localDataSource.getUsername()
    }

    override suspend fun saveUser(user: ImgurUser) {
        cache(user).let { localDataSource.saveUser(it) }
    }

    override suspend fun refreshImages() {
        cacheIsDirty = true
    }

    private suspend fun getFromRemoteDataSource(subreddit: String, page: Int): Result<List<ImgurImage>> {
        val result = remoteDataSource.getImages(subreddit, page)
        return when (result) {
            is Result.Success -> {
                refreshCache(result.data)
                refreshLocalDataSource(result.data)
                Result.Success(ArrayList(cachedImages.values))
            }
            is Result.Error -> Result.Error(RemoteDataNotFoundException())
        }

    }

    override suspend fun deleteImages() {
        remoteDataSource.deleteImages()
        localDataSource.deleteImages()
        cachedImages.clear()
    }

    override suspend fun saveImages(images: List<ImgurImage>) {
        localDataSource.saveImages(images)
    }

    private suspend fun refreshLocalDataSource(images: List<ImgurImage>) {
        localDataSource.deleteImages()
        localDataSource.saveImages(images)
    }

    private fun refreshCache(images: List<ImgurImage>) {
        cachedImages.clear()
        images.forEach { cache(it) }
        cacheIsDirty = false
    }

    private fun cache(image: ImgurImage): ImgurImage {
        val cachedImg = image.copy()
        cachedImages[cachedImg.id] = cachedImg
        return cachedImg
    }

    private fun cache(user: ImgurUser): ImgurUser {
        val next = user.copy()
        cachedUsers = next
        return next
    }

    companion object {

        private var INSTANCE: ImgurRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         * @param token the access token for the imgur API if you want to make authenticated calls.
         *
         * @param remoteDataSource the backend data source
         * *
         * @param localDataSource  the device storage data source
         * *
         * @return the [ImgurRepository] instance
         */
        @JvmStatic
        fun getInstance(token: String = "",
                        remoteDataSource: ImgurDataSource = ImgurRemoteDataSource.getInstance(token),
                        localDataSource: ImgurDataSource = ImgurLocalDataSource.getInstance()): ImgurRepository {
            return INSTANCE ?: ImgurRepository(remoteDataSource, localDataSource)
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}