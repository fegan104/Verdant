package com.frankegan.verdant.api

import android.text.TextUtils
import com.frankegan.verdant.BuildConfig
import com.frankegan.verdant.models.ImgurImage
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


/**
 * Created by frankegan on 3/5/18.
 */
interface ImgurService {

    companion object {
        fun create(authToken: String = ""): ImgurService {
            val interceptor = Interceptor { chain ->
                val newRequest = chain.request().newBuilder().apply {
                    if (TextUtils.isEmpty(authToken)) {
                        addHeader("Authorization", "Client-ID ${BuildConfig.IMGUR_CLIENT_ID}")
                        return@apply
                    }
                    addHeader("Authorization", "Bearer $authToken")
                }.build()
                return@Interceptor chain.proceed(newRequest)
            }
            // Add the interceptor to OkHttpClient
            val client = OkHttpClient.Builder().apply { interceptors().add(interceptor) }.build()

            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.imgur.com/3/")
                    .client(client)
                    .build()

            return retrofit.create(ImgurService::class.java)
        }
    }

    @GET("gallery/r/{subreddit}/{page}.json")
    fun listImages(@Path("subreddit") subreddit: String,
                   @Path("page") page: Int): Call<List<ImgurImage>>

    @POST("image/{id}/favorite")
    fun toggleFavoriteImage(@Path("id") id: String)
}