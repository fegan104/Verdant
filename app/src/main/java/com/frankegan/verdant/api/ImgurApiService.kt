package com.frankegan.verdant.api

import android.content.Context
import com.frankegan.verdant.BuildConfig
import com.frankegan.verdant.ImgurAPI
import com.frankegan.verdant.VerdantApp
import com.frankegan.verdant.models.ApiResponse
import com.frankegan.verdant.models.ImgurImage
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


/**
 * Created by frankegan on 3/5/18.
 */
interface ImgurApiService {

    companion object {
        /**
         * Used to make sure we don't misspell "accces_tokn".
         */
        private val ACCESSTOKEN = "access_token"

        fun create(): ImgurApiService {
            val token = VerdantApp.instance
                    .getSharedPreferences(ImgurAPI.PREFS_NAME, Context.MODE_PRIVATE)
                    .getString(ACCESSTOKEN, "")

            val interceptor = Interceptor { chain ->
                val newRequest = chain.request().newBuilder().apply {
                    if (token.isEmpty()) {
                        addHeader("Authorization", "Client-ID ${BuildConfig.IMGUR_CLIENT_ID}")
                        return@apply
                    }
                    addHeader("Authorization", "Bearer $token")
                }.build()
                return@Interceptor chain.proceed(newRequest)
            }
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            // Add the interceptor to OkHttpClient
            val client = OkHttpClient.Builder().apply {
                interceptors().addAll(listOf(interceptor, logging))
            }.build()

            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.imgur.com/3/")
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .client(client)
                    .build()

            return retrofit.create(ImgurApiService::class.java)
        }
    }

    @GET("gallery/r/{subreddit}/{page}.json")
    fun listImages(@Path("subreddit") subreddit: String,
                   @Path("page") page: Int): Deferred<ApiResponse<List<ImgurImage>>>

    @POST("image/{id}/favorite")
    fun toggleFavoriteImage(@Path("id") id: String): Deferred<ApiResponse<String>>
}