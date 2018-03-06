package com.frankegan.verdant

import android.app.Application
import android.content.ContextWrapper
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.pixplicity.easyprefs.library.Prefs

/**
 * @author frankegan created on 6/2/15.
 */
class VerdantApp : Application() {


    override fun onCreate() {
        super.onCreate()
        volleyRequestQueue = Volley.newRequestQueue(this)
        instance = this
        // Initialize the Prefs class
        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }

    companion object {

        @JvmStatic
        lateinit var volleyRequestQueue: RequestQueue
            private set

        @JvmStatic
        lateinit var instance: VerdantApp
            private set
    }
}