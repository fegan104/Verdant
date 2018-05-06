package com.frankegan.verdant.data.remote

import com.frankegan.verdant.data.ImgurDataSource
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.data.ImgurUser
import com.frankegan.verdant.data.Result

class ImgurRemoteDatasource : ImgurDataSource {
    override suspend fun getImage(id: String): Result<ImgurImage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getImages(subreddit: String, page: Int): Result<List<ImgurImage>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun saveImage(image: ImgurImage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun favoriteImage(id: String): Result<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUsername(): Result<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun saveUser(user: ImgurUser) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun refreshImages() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}