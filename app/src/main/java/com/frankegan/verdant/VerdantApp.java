package com.frankegan.verdant;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
    }

    public static Context getContext() {
        return instance;
    }

    public static RequestQueue getVolleyRequestQueue(){
        return mRequestQueue;
    }
}