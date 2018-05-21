package com.frankegan.verdant

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.systemService


/**
 * @author frankegan created on 6/2/15.
 */
class VerdantApp : Application() {

    companion object {

        @JvmStatic
        lateinit var instance: VerdantApp
            private set

        const val DOWNLOAD_CHANNEL_ID = "download_channel_is"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initChannels()
    }

    private fun initChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        /// Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = getString(R.string.download_channel_name)
        val desc = getString(R.string.download_channel_desc)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(DOWNLOAD_CHANNEL_ID, name, importance).apply { description = desc }
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        systemService<NotificationManager>().createNotificationChannel(channel)
    }
}