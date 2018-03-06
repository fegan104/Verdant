package com.frankegan.verdant.api

import com.frankegan.verdant.BuildConfig
import com.frankegan.verdant.models.ImgurImage
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * Created by frankegan on 3/5/18.
 */
interface ImgurService {

    companion object {
        fun create(): ImgurService {
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.imgur.com/3/")
                    .build()

            return retrofit.create(ImgurService::class.java)
        }
    }

    @Headers("Authorization Client-ID ${BuildConfig.IMGUR_CLIENT_ID}")
    @GET("gallery/r/{subreddit}/{page}.json")
    fun listImages(@Path("subreddit") subreddit: String,
                   @Path("page") page: Int): Call<List<ImgurImage>>
}