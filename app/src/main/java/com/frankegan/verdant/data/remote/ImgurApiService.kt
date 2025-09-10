package com.frankegan.verdant.data.remote

import com.frankegan.verdant.BuildConfig
import com.frankegan.verdant.data.ApiResponse
import com.frankegan.verdant.data.ImgurImage
import kotlinx.coroutines.Deferred
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

        fun create(token: String): ImgurApiService {

            val authInterceptor = Interceptor { chain ->
                val newRequest = chain.request().newBuilder().apply {
                    if (token.isEmpty()) {
                        addHeader("Authorization", "Client-ID ${BuildConfig.IMGUR_CLIENT_ID}")
                        return@apply
                    }
                    addHeader("Authorization", "Bearer $token")
                }.build()
                return@Interceptor chain.proceed(newRequest)
            }
            val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            // Add the interceptor to OkHttpClient
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.imgur.com/3/")
                .client(client)
                .build()

            return retrofit.create(ImgurApiService::class.java)
        }
    }

    @GET("image/{id}/{page}.json")
    suspend fun getImage(@Path("id") id: String): ApiResponse<ImgurImage>

    @GET("gallery/r/{subredditLiveData}/{page}.json")
    suspend fun listImages(
        @Path("subredditLiveData") subreddit: String,
        @Path("page") page: Int,
    ): ApiResponse<List<ImgurImage>>

    @POST("image/{id}/favorite")
    fun toggleFavoriteImage(@Path("id") id: String): ApiResponse<String>
}