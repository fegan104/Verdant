package com.frankegan.verdant.feature.imagedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.frankegan.verdant.data.ImgurDataSource
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.data.ImgurRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Dispatcher

class ImageDetailViewModel(private val imageId: String) : ViewModel() {
    val imgurRepo: ImgurDataSource = ImgurRepository.getInstance()

    val imgurImage: Flow<Result<ImgurImage>> = flow {
        emit(imgurRepo.getImage(imageId))
    }.flowOn(Dispatchers.IO)

    @Suppress("UNCHECKED_CAST")
    class Factory(private val imageId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ImageDetailViewModel::class.java)) {
                return ImageDetailViewModel(imageId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
