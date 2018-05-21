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
    val images: MutableLiveData<List<ImgurImage>> = MutableLiveData()
    val subreddit: MutableLiveData<String> = MutableLiveData()
    val recents: MutableLiveData<List<String>> = MutableLiveData()

    private val token by lazy { androidContext.defaultSharedPreferences.getString("access_token", "") }
    private val imgurRepository by lazy { ImgurRepository.getInstance(token) }
    val defaultSub: String by lazy {
        androidContext.defaultSharedPreferences
                .getString("default_sub", androidContext.getString(R.string.itap_sub))
    }

    fun subscribe(renderer: (LiveData<List<ImgurImage>>, LiveData<String>, LiveData<List<String>>) -> Unit) {
        renderer(images, subreddit, recents)
    }

    /**
     * Loads the next page of images as user scrolls.
     *
     * @param page to be loaded, starts at 0.
     */
    fun loadMoreImages(page: Int) = launchSilent(UI) {
        try {
            val foo = imgurRepository.getImages(subreddit.value ?: defaultSub, page)
            Log.d("HomeViewModel", foo.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val result = imgurRepository.getImages(subreddit.value ?: defaultSub, page)
        when (result) {
            is Result.Success -> {
                images.value = result.data
            }
            is Result.Error -> {
                Log.d("HomeViewModel", result.exception.message)
            }
        }
    }

    fun changeSubreddit(subName: String) {
        //recover list
        // TODO make this a db call form repo
        recents.value = ArrayList(androidContext.defaultSharedPreferences
                .getStringSet("recent_subreddits", HashSet()))
                .apply { add(0, subName) }
        androidContext.defaultSharedPreferences.edit {
            putStringSet("recent_subreddits", HashSet(recents.value))
        }
        //update changes
        subreddit.value = subName
    }
}