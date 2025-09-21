package com.frankegan.verdant.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.frankegan.verdant.data.local.ImgurLocalDataSource
import com.frankegan.verdant.data.local.VerdantDatabase
import com.frankegan.verdant.data.remote.ImgurRemoteDataSource
import kotlinx.coroutines.flow.Flow

class ImgurRepository private constructor(
    private val remoteDataSource: ImgurDataSource,
    private val localDataSource: ImgurDataSource,
) : ImgurDataSource {

    override suspend fun getImage(id: String): Result<ImgurImage> {
        // Is the task in the local data source? If not, query the network.
        val localResult = localDataSource.getImage(id)
        return localResult.recoverCatching {
            remoteDataSource.getImage(id)
                .onSuccess { img -> localDataSource.saveImages(listOf(img)) }
                .getOrThrow()
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun observeImagePaging(currentSubreddit: String): Flow<PagingData<ImgurImage>> {
        val pagingSourceFactory = { VerdantDatabase.getInstance().imageDao().getAllPages() }

        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = 1,
                enablePlaceholders = false,
                initialLoadSize = DEFAULT_PAGE_SIZE,
            ),
            remoteMediator = ArticleRemoteMediator(
                currentSubreddit,
                service = (remoteDataSource as ImgurRemoteDataSource).apiService,
                database = VerdantDatabase.getInstance(),
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun favoriteImage(image: ImgurImage): Result<String> {
        val imageWithFavoriteToggled = image.copy(favorite = !image.favorite)
        remoteDataSource.favoriteImage(imageWithFavoriteToggled)
        return localDataSource.favoriteImage(imageWithFavoriteToggled)
    }

    override suspend fun getUsername(): Result<String> {
        return localDataSource.getUsername()
    }

    override suspend fun saveUser(user: ImgurUser) {
        localDataSource.saveUser(user)
    }

    override suspend fun refreshImages() {
        localDataSource.deleteImages()
    }

    override suspend fun deleteImages() {
        localDataSource.deleteImages()
    }

    override suspend fun saveImages(images: List<ImgurImage>) {
        localDataSource.saveImages(images)
    }

    companion object {

        /**
         * Imgur's endpoint is hardcoded to return 100 items at a time.
         */
        private const val DEFAULT_PAGE_SIZE = 100

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
        fun getInstance(
            token: String = "",
            remoteDataSource: ImgurDataSource = ImgurRemoteDataSource.getInstance(token),
            localDataSource: ImgurDataSource = ImgurLocalDataSource.getInstance()
        ): ImgurRepository {
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