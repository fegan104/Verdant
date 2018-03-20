package com.frankegan.verdant.home

import androidx.content.edit
import com.android.volley.Response
import com.frankegan.verdant.ImgurAPI
import com.frankegan.verdant.VerdantApp
import org.jetbrains.anko.defaultSharedPreferences

/**
 * Created by frankegan on 5/10/16.
 */
class HomePresenter(override var subreddit: String, private val homeView: HomeContract.View)
    : HomeContract.UserActionsListener {

    /**
     * Loads the next page of images as user scrolls.
     *
     * @param page to be loaded, starts at 0.
     */
    override fun loadMoreImages(page: Int) {
        homeView.setProgressIndicator(true)
        homeView.setToolbarTitle(subreddit)
        ImgurAPI.loadPage(
                Response.Listener { r ->
                    homeView.showImages(r)
                    homeView.setProgressIndicator(false)
                },
                Response.ErrorListener({ it.printStackTrace() }),
                subreddit,
                page)
    }

    override fun changeSubreddit(subName: String) {
        homeView.showBottomSheet(false)
        //recover list
        val recents = ArrayList(VerdantApp.instance.defaultSharedPreferences
                .getStringSet("recent_subreddits", HashSet()))
        recents.add(0, subName)
        VerdantApp.instance.defaultSharedPreferences.edit {
            putStringSet("recent_subreddits", HashSet(recents))
        }
        //update changes
        homeView.refreshRecents()
        homeView.clearImages()
        this.subreddit = subName
        loadMoreImages(0)
    }
}
