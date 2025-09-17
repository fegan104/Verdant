package com.frankegan.verdant.data

import com.frankegan.verdant.data.remote.RedditSearchRemoteDataSource

class RedditSearchRepository private constructor(
    private val remoteDataSource: RedditSearchRemoteDataSource,
) {

    suspend fun searchGalleries(query: String): List<SubredditInfo> {
        return remoteDataSource.searchGalleries(query)
    }

    companion object {

        private var INSTANCE: RedditSearchRepository? = null

        fun getInstance(): RedditSearchRepository {
            val remoteDataSource = RedditSearchRemoteDataSource()
            return INSTANCE ?: RedditSearchRepository(remoteDataSource).also {
                INSTANCE = it
            }
        }
    }
}