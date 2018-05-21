package com.frankegan.verdant.customtabs

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.customtabs.CustomTabsSession

/**
 * Created by frankegan on 4/14/16.
 *
 * This is a helper class to manage the connection to the Custom Tabs Service and
 *
 * Adapted from github.com/GoogleChrome/custom-tabs-client
 */
class CustomTabActivityHelper : LifecycleObserver {
    private var mCustomTabsSession: CustomTabsSession? = null
    private var mClient: CustomTabsClient? = null
    private var mConnection: CustomTabsServiceConnection? = null
    private var mConnectionCallback: ConnectionCallback? = null

    /**
     * Creates or retrieves an exiting CustomTabsSession
     *
     * @return a CustomTabsSession
     */
    val session: CustomTabsSession?
        get() {
            if (mClient == null) {
                mCustomTabsSession = null
            } else if (mCustomTabsSession == null) {
                mCustomTabsSession = mClient!!.newSession(null)
            }
            return mCustomTabsSession
        }

    /**
     * Binds the Activity to the Custom Tabs Service
     * @param activity the activity to be bound to the service
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun bindCustomTabsService(owner: LifecycleOwner) {
        val activity = owner as Activity
        if (mClient != null) return

        val packageName = CustomTabsHelper.getPackageNameToUse(activity) ?: return
        mConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                mClient = client
                mClient!!.warmup(0L)
                if (mConnectionCallback != null) mConnectionCallback!!.onCustomTabsConnected()
                //Initialize a session as soon as possible.
                session
            }

            override fun onServiceDisconnected(name: ComponentName) {
                mClient = null
                if (mConnectionCallback != null) mConnectionCallback!!.onCustomTabsDisconnected()
            }
        }
        CustomTabsClient.bindCustomTabsService(activity, packageName, mConnection)
    }

    /**
     * Unbinds the Activity from the Custom Tabs Service
     * @param activity the activity that is bound to the service
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unbindCustomTabsService(owner: LifecycleOwner) {
        val activity = owner as Activity
        if (mConnection == null) return

        activity.unbindService(mConnection)
        mClient = null
        mCustomTabsSession = null
    }

    /**
     * @see {@link CustomTabsSession.mayLaunchUrl
     * @return true if call to mayLaunchUrl was accepted
     */
    fun mayLaunchUrl(uri: Uri, extras: Bundle? = null, otherLikelyBundles: List<Bundle>? = null): Boolean {
        if (mClient == null) return false

        val session = session ?: return false

        return session.mayLaunchUrl(uri, extras, otherLikelyBundles)
    }

    /**
     * A Callback for when the service is connected or disconnected. Use those callbacks to
     * handle UI changes when the service is connected or disconnected
     */
    interface ConnectionCallback {
        /**
         * Called when the service is connected
         */
        fun onCustomTabsConnected()

        /**
         * Called when the service is disconnected
         */
        fun onCustomTabsDisconnected()
    }

    companion object {

        /**
         * Opens the URL on a Custom Tab if possible; otherwise falls back to opening it via
         * `Intent.ACTION_VIEW`
         *
         * @param activity The host activity
         * @param customTabsIntent a CustomTabsIntent to be used if Custom Tabs is available
         * @param uri the Uri to be opened
         */
        fun openCustomTab(activity: Activity,
                          customTabsIntent: CustomTabsIntent,
                          uri: Uri) {
            val packageName = CustomTabsHelper.getPackageNameToUse(activity)

            // if we cant find a package name, it means there's no browser that supports
            // Custom Tabs installed. So, we fallback to a view intent
            if (packageName != null) {
                customTabsIntent.intent.`package` = packageName
                customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                customTabsIntent.launchUrl(activity, uri)
            } else {
                activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
    }

}
