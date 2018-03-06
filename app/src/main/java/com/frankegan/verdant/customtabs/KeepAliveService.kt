package com.frankegan.verdant.customtabs


import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Created by frankegan on 4/14/16.
 *
 * Empty service used by the custom tab to bind to, raising the application's importance.
 *
 * Adapted from github.com/GoogleChrome/custom-tabs-client
 */
class KeepAliveService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return sBinder
    }

    companion object {
        private val sBinder = Binder()
    }
}