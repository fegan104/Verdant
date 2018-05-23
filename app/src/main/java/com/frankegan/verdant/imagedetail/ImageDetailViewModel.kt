package com.frankegan.verdant.imagedetail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import androidx.work.WorkManager
import androidx.work.WorkStatus
import androidx.work.ktx.OneTimeWorkRequestBuilder
import androidx.work.ktx.toWorkData
import com.frankegan.verdant.SingleLiveEvent
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.data.ImgurRepository
import com.frankegan.verdant.data.Result
import com.frankegan.verdant.utils.launchSilent
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.defaultSharedPreferences

/**
 * Created by frankegan on 5/14/16.
 */
class ImageDetailViewModel(private val appContext: Application) : AndroidViewModel(appContext) {
    private val token by lazy { appContext.defaultSharedPreferences.getString("access_token", "") }
    private val imgurRepository by lazy { ImgurRepository.getInstance(token) }

    private val imageLiveData = MutableLiveData<ImgurImage>()
    private val snackbarLiveData = SingleLiveEvent<String>()
    private var workerLiveData = MediatorLiveData<WorkStatus>()

    fun subscribe(renderer: (LiveData<ImgurImage>,
                             SingleLiveEvent<String>,
                             LiveData<WorkStatus>) -> Unit) {
        renderer(imageLiveData, snackbarLiveData, workerLiveData)
    }

    fun loadImage(id: String) = launchSilent(UI) {
        val result = imgurRepository.getImage(id)
        when (result) {
            is Result.Success -> {
                imageLiveData.value = result.data
            }
            is Result.Error -> {
                snackbarLiveData.value = result.exception.message
            }
        }
    }

    fun toggleFavoriteImage(image: ImgurImage) = launchSilent(UI) {
        val res = imgurRepository.favoriteImage(image)
        //TODO let the user know if they aren't signed in
        when (res) {
            is Result.Success -> {
                val favorited = res.data == "favorite"
                val msg = if (favorited) "Favorited  ❤️" else "Unfavorited 💔"
                snackbarLiveData.value = msg
                imageLiveData.value = image.copy(favorite = favorited)
            }
            is Result.Error -> {
                snackbarLiveData.value = res.exception.message
            }
        }
    }

    fun downloadImage() {
        val args = mapOf(
                ImageDownloadWorker.KEY_LINK_ARG to imageLiveData.value?.link
        ).toWorkData()

        val downloadWork = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
                .setInputData(args)
                .addTag(DOWNLOAD_WORKER_TAG)
                .build()

        val workManager = WorkManager.getInstance().apply { enqueue(downloadWork) }

        workerLiveData.addSource(workManager.getStatusById(downloadWork.id)) { value ->
            workerLiveData.value = value
        }
    }

    companion object {
        /**
         * work manager tag for downloading imagesLiveData
         */
        const val DOWNLOAD_WORKER_TAG = "download_worker_tag"
    }
}
