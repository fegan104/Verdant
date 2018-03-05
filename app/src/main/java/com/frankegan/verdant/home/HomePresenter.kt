package com.frankegan.verdant.home

import com.android.volley.Response
import com.frankegan.verdant.ImgurAPI
import com.frankegan.verdant.models.ImgurImage
import com.pixplicity.easyprefs.library.Prefs
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by frankegan on 5/10/16.
 */
class HomePresenter(override var subreddit: String, private val homeView: HomeContract.View)
    : HomeContract.UserActionsListener {

    /**
     * Loads the next page of images as user scrolls.
     *
     * @param newPage page to be loaded, starts at 0.
     */
    override fun loadMoreImages(page: Int) {
        homeView.setProgressIndicator(true)
        homeView.setToolbarTitle(subreddit)
        ImgurAPI.loadPage(
                Response.Listener { r ->
                    homeView.showImages(jsonToList(r))
                    homeView.setProgressIndicator(false)
                },
                Response.ErrorListener({ it.printStackTrace() }),
                subreddit,
                page)
    }

    override fun changeSubreddit(subName: String) {
        homeView.showBottomSheet(false)
        //recover list
        val recents = ArrayList(Prefs.getStringSet("recent_subreddits", HashSet()))
        recents.add(0, subName)
        //save edited list
        Prefs.putStringSet("recent_subreddits", HashSet(recents))
        //update changes
        homeView.refreshRecents()
        homeView.clearImages()
        this.subreddit = subName
        loadMoreImages(0)
    }

    /**
     * Converts a [JSONObject] to a [List] of [ImgurImage]s.
     *
     * @param object The response object from Imgur.
     * @return a list of parsed [ImgurImage]s.
     */
    private fun jsonToList(obj: JSONObject): List<ImgurImage> {
        val images = ArrayList<ImgurImage>()
        val responseJSONArray: JSONArray
        try {
            responseJSONArray = obj.getJSONArray("data")
            for (i in 0 until responseJSONArray.length()) {
                val responseObj = responseJSONArray.getJSONObject(i)
                val datum = ImgurImage(
                        responseObj.getString("id"),
                        responseObj.getString("title"),
                        responseObj.getString("description"),
                        responseObj.getBoolean("animated"),
                        responseObj.getInt("views"))
                images.add(datum)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return images
    }
}
