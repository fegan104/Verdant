package com.frankegan.verdant

import android.app.Application
import android.arch.persistence.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.frankegan.verdant.data.local.VerdantDatabase


/**
 * @author frankegan created on 6/2/15.
 */
class VerdantApp : Application() {

    companion object {

        @JvmStatic
        lateinit var volleyRequestQueue: RequestQueue
            private set

        @JvmStatic
        lateinit var instance: VerdantApp
            private set

        @JvmStatic
        lateinit var db: VerdantDatabase
            private set

    }

    override fun onCreate() {
        super.onCreate()
        volleyRequestQueue = Volley.newRequestQueue(this)
        instance = this
        db = Room.databaseBuilder(this, VerdantDatabase::class.java, "verdant-database").build()
    }
}