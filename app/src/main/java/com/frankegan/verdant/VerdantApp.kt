package com.frankegan.verdant

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * @author frankegan created on 6/2/15.
 */
class VerdantApp : Application() {


    override fun onCreate() {
        super.onCreate()
        volleyRequestQueue = Volley.newRequestQueue(this)
        instance = this
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