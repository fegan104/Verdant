package com.frankegan.verdant.data

import androidx.compose.runtime.invalidateGroupsWithKey
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.frankegan.verdant.data.local.VerdantDatabase
import com.frankegan.verdant.data.remote.ImgurApiService
import kotlin.collections.firstOrNull
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val subreddit: String,
    private val service: ImgurApiService, // Retrofit or whatever
    private val database: VerdantDatabase
) : RemoteMediator<Int, ImgurImage>() {

    private val imagesDao = database.imageDao()
    private val keysDao = database.imagePagingKeyDao()

//    override suspend fun initialize(): InitializeAction {
//        return super.initialize()
//    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ImgurImage>
    ): MediatorResult {
        try {
            // Figure out page number
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 0
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            // Network call
            val apiResponse = service.listImages(page = page, subreddit = subreddit)
            val articles = apiResponse.data
            val endOfPaginationReached = articles.isEmpty()

            // DB transaction
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keysDao.clearRemoteKeys()
                    imagesDao.deleteImages()
                }
                val keys = articles.map {
                    ImagePagingKey(
                        imageId = it.id,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }
                keysDao.insertAll(keys)
                imagesDao.insertAll(*articles.toTypedArray())
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    // Helpers for pagination keys
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ImgurImage>): ImagePagingKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { article -> keysDao.remoteKeysArticleId(article.id) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ImgurImage>): ImagePagingKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { article -> keysDao.remoteKeysArticleId(article.id) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ImgurImage>
    ): ImagePagingKey? {
        val anchorPosition = state.anchorPosition ?: return null
        val article = state.closestItemToPosition(anchorPosition) ?: return null
        return keysDao.remoteKeysArticleId(article.id)
    }
}
