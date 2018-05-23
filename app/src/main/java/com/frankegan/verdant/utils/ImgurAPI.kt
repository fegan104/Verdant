package com.frankegan.verdant.utils

import android.app.Activity
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsSession
import android.support.v4.content.ContextCompat
import com.frankegan.verdant.BuildConfig
import com.frankegan.verdant.R
import com.frankegan.verdant.customtabs.CustomTabActivityHelper

/**
 * @author frankegan created on 10/24/15.
 */
object ImgurAPI {
    val IMGUR_CLIENT_ID = BuildConfig.IMGUR_CLIENT_ID
    val LOGIN_URL = "https://api.imgur.com/oauth2/authorize?client_id=${IMGUR_CLIENT_ID}&response_type=token"

    /**
     * Calling this method will initiate a login flow hat end with the user either logging in or declining.
     *
     * @param host    The host activity you are calling from.
     * @param session The CustomTabSession, this is only useful you were planning on warming up tab or something like that.
     */
    @JvmStatic
    fun login(host: Activity, session: CustomTabsSession?) {
//        TODO("find a final place for this")
        val customTabsIntent = CustomTabsIntent.Builder(session)
                .setToolbarColor(ContextCompat.getColor(host, R.color.material_lightgreen500))
                .build()

        CustomTabActivityHelper.openCustomTab(host,
                customTabsIntent,
                Uri.parse(LOGIN_URL))
    }
}