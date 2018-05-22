package com.frankegan.verdant.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import androidx.core.content.edit
import com.frankegan.verdant.R
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.data.ImgurRepository
import com.frankegan.verdant.data.Result
import com.frankegan.verdant.utils.launchSilent
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.defaultSharedPreferences

class HomeViewModel(private val androidContext: Application) : AndroidViewModel(androidContext) {
    val imagesLiveData: MutableLiveData<List<ImgurImage>> = MutableLiveData()
    val subredditLiveData: MutableLiveData<String> = MutableLiveData()
    val recentsLiveData = MutableLiveData<List<String>>().apply {
        value = ArrayList(androidContext.defaultSharedPreferences
                .getStringSet("recent_subreddits", HashSet()))
    }

    private val token by lazy { androidContext.defaultSharedPreferences.getString("access_token", "") }
    private val imgurRepository by lazy { ImgurRepository.getInstance(token) }
    val defaultSub: String by lazy {
        androidContext.defaultSharedPreferences
                .getString("default_sub", androidContext.getString(R.string.itap_sub))
    }
    val recents by lazy {
        androidContext
                .defaultSharedPreferences
                .getStringSet("recent_subreddits", HashSet())
    }

    fun subscribe(renderer: (LiveData<List<ImgurImage>>, LiveData<String>, LiveData<List<String>>) -> Unit) {
        renderer(imagesLiveData, subredditLiveData, recentsLiveData)
    }

    /**
     * Loads the next page of imagesLiveData as user scrolls.
     *
     * @param page to be loaded, starts at 0.
     */
    fun loadMoreImages(page: Int) = launchSilent(UI) {
        val result = imgurRepository.getImages(subredditLiveData.value ?: defaultSub, page)
        when (result) {
            is Result.Success -> {
                imagesLiveData.value = result.data
            }
            is Result.Error -> {
                Log.d("HomeViewModel", result.exception.message)
            }
        }
    }

    fun changeSubreddit(subName: String) = launchSilent(UI) {
        //recover list
        androidContext.defaultSharedPreferences.edit {
            putStringSet("recent_subreddits", recents.apply { add(subName) })
        }
        recentsLiveData.value = recents.toList()
        //update changes
        imgurRepository.deleteImages()
        subredditLiveData.value = subName
    }
}