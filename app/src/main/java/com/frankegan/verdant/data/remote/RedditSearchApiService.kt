package com.frankegan.verdant.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.frankegan.verdant.data.SubredditSearchResponse

interface RedditSearchApiService {
    @GET("/subreddits/search.json")
    suspend fun searchSubreddits(
        @Query("q") query: String,
        @Query("limit") limit: Int = 25
    ): SubredditSearchResponse
}
