package com.frankegan.verdant.data.remote

import com.frankegan.verdant.data.SubredditInfo
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlin.jvm.java

class RedditSearchRemoteDataSource {

    private val json = Json {
        ignoreUnknownKeys = true // Reddit sends a lot of extra fields
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.reddit.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api = retrofit.create(RedditSearchApiService::class.java)

    suspend fun searchGalleries(query: String): List<SubredditInfo> {
        if (query.isEmpty()) return emptyList()
        // Call from a coroutine
        val result = api.searchSubreddits(query)
        return result.data.children.map { it.data }
    }
}