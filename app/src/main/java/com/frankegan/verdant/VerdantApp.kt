package com.frankegan.verdant

import android.app.Application


/**
 * @author frankegan created on 6/2/15.
 */
class VerdantApp : Application() {

    companion object {

        @JvmStatic
        lateinit var instance: VerdantApp
            private set

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}