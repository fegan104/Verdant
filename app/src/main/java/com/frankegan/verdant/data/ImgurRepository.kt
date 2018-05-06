package com.frankegan.verdant.data

import com.frankegan.verdant.data.local.ImgurLocalDataSource
import com.frankegan.verdant.data.remote.ImgurRemoteDatasource

class ImgurRepository(
        val remoteDataSource: ImgurDataSource,
        val localDataSource: ImgurDataSource
) : ImgurDataSource {

    /**
     * The in memory cache of our imgur data.
     */
    val cachedImages = LinkedHashMap<String, ImgurImage>()

    override suspend fun getImage(id: String): Result<ImgurImage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getImages(subreddit: String, page: Int): Result<List<ImgurImage>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun saveImage(image: ImgurImage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun favoriteImage(id: String): Result<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUsername(): Result<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun saveUser(user: ImgurUser) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun refreshImages() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun refreshCache() {
        TODO("not implemented")
    }

    private suspend fun refreshLocalDataSource(images: List<ImgurImage>) {
        TODO("implement")
    }

    private fun cache(image: ImgurImage): ImgurImage {
        TODO("not implemented")
    }

    companion object {

        private var INSTANCE: ImgurRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         * @param tasksRemoteDataSource the backend data source
         * *
         * @param tasksLocalDataSource  the device storage data source
         * *
         * @return the [TasksRepository] instance
         */
        @JvmStatic
        fun getInstance(remoteDataSource: ImgurDataSource = ImgurRemoteDatasource(),
                        localDataSource: ImgurDataSource = ImgurLocalDataSource()): ImgurRepository {
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