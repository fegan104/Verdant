package com.frankegan.verdant.data

interface ImgurDataSource {
    suspend fun getImage(id: String): Result<ImgurImage>

    suspend fun getImages(subreddit: String, page: Int): Result<List<ImgurImage>>

    suspend fun favoriteImage(image: ImgurImage): Result<String>

    suspend fun getUsername(): Result<String>

    suspend fun saveUser(user: ImgurUser)

    suspend fun refreshImages()

    suspend fun deleteImages()

    suspend fun saveImages(images: List<ImgurImage>)
}