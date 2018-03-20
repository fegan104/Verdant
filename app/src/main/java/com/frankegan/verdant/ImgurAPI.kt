package com.frankegan.verdant

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsSession
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.android.volley.Response
import com.frankegan.verdant.api.ImgurApiService
import com.frankegan.verdant.customtabs.CustomTabActivityHelper
import com.frankegan.verdant.models.ImgurImage
import com.frankegan.verdant.models.ImgurUser
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync

/**
 * @author frankegan created on 10/24/15.
 */
object ImgurAPI {
    /**
     * Name for saving data to shared preferences.
     */
    @JvmStatic
    val PREFS_NAME = "imgur_auth"
    /**
     * Granted by Imgur for developing, need to make requests.
     */
    @JvmStatic
    private val IMGUR_CLIENT_ID = BuildConfig.IMGUR_CLIENT_ID
    /**
     * Redirect URL specified in Imgur developer console.
     */
    @JvmStatic
    val IMGUR_REDIRECT_URL = "verdant://logincallback"
    /**
     * The URL that we call inorder to authenticate.
     */
    @JvmStatic
    val LOGIN_URL = ("https://api.imgur.com/oauth2/authorize?client_id="
            + ImgurAPI.IMGUR_CLIENT_ID
            + "&response_type=token")

    @JvmStatic
    private val ACCESSTOKEN = "access_token"
    /**
     * Is the current user logged in?
     *
     * @return whether the user is logged in.
     */
    @JvmStatic
    val isLoggedIn: Boolean
        get() {
            val context = VerdantApp.instance
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return !TextUtils.isEmpty(prefs.getString("refresh_token", null))
        }

    /**
     * Deletes all the data we have saved for our user. This means they will have to login again to use their account.
     */
    @JvmStatic
    fun logout() {
        val context = VerdantApp.instance
        context.getSharedPreferences(PREFS_NAME, 0)
                .edit()
                .clear()
                .commit()
    }


    /**
     * Saves data from a access token request. Saves Refresh Token, Access Token, expiration time, token type, and account User name.
     *
     * @param user The access user we're saving.
     */
    @JvmStatic
    fun saveResponse(user: ImgurUser) {
        doAsync { VerdantApp.db.userDao().insertAll(user) }

        val context = VerdantApp.instance
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit().clear().apply()
        prefs.edit()
                .putString("access_token", user.accessToken)
                .putString("refresh_token", user.refreshToken)
                .putLong("expires_in", user.expiresAt)
                .putString("account_username", user.username)
                .apply()
    }

    /**
     * Loads a page of photos.
     *
     *
     *
     * @param success   The success response handler.
     * @param error     The error response handler.
     * @param subreddit The subreddit we want to laod for.
     * @param newPage   The page we want to load.
     */
    @JvmStatic
    fun loadPage(success: Response.Listener<List<ImgurImage>>,
                 error: Response.ErrorListener,
                 subreddit: String,
                 newPage: Int) = async(UI) {

        val apiRes = ImgurApiService
                .create()
                .listImages(subreddit, newPage)
                .await()
        success.onResponse(apiRes.data)
    }


    /**
     * Calling this method will initiate a login flow hat end with the user either logging in or declining.
     *
     * @param host    The host activity you are calling from.
     * @param session The CustomTabSession, this is only useful you were planning on warming up tab or something like that.
     */
    @JvmStatic
    fun login(host: Activity, session: CustomTabsSession?) {
        val customTabsIntent = CustomTabsIntent.Builder(session)
                .setToolbarColor(ContextCompat.getColor(host, R.color.material_lightgreen500))
                .build()

        CustomTabActivityHelper.openCustomTab(host,
                customTabsIntent,
                Uri.parse(LOGIN_URL))
    }

    /**
     * Default subbredit name because it's kinda pretty.
     */
    @JvmStatic
    val defaultSubreddit: String
        get() {
            val context = VerdantApp.instance
            return context.defaultSharedPreferences
                    .getString("default_sub", context.getString(R.string.itap_sub))
        }

    /**
     * @return The account name of the current user or null if not logged in.
     */
    @JvmStatic
    val accountName: LiveData<List<ImgurUser>> = VerdantApp.db.userDao().getAll()
}