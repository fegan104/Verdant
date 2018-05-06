package com.frankegan.verdant.data

interface ImgurDataSource {
    suspend fun getImage(id: String): Result<ImgurImage>

    suspend fun getImages(subreddit: String, page: Int): Result<List<ImgurImage>>

    suspend fun saveImage(image: ImgurImage)

    suspend fun favoriteImage(id: String): Result<String>

    suspend fun getUsername(): Result<String>

    suspend fun saveUser(user: ImgurUser)

    suspend fun refreshImages()
}