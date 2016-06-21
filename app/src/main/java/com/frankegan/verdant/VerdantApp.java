package com.frankegan.verdant;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * @author frankegan created on 6/2/15.
 */
public class VerdantApp extends Application {

    private static VerdantApp instance;
    private static RequestQueue mRequestQueue;

    public VerdantApp() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public static Context getContext() {
        return instance;
    }

    public static RequestQueue getVolleyRequestQueue(){
        return mRequestQueue;
    }
}