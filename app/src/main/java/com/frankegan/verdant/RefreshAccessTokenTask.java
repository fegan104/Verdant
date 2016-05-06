package com.frankegan.verdant;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author frankegan created on 10/24/15.
 */
public class RefreshAccessTokenTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = RefreshAccessTokenTask.class.getSimpleName();

    @Override
    protected String doInBackground(Void... params) {
        String accessToken = ImgurAPI.getInstance().refreshAccessToken();
        if (!TextUtils.isEmpty(accessToken)) {
            Log.i(TAG, "Got new access token");
        }
        else {
            Log.i(TAG, "Could not get new access token");
        }
        return accessToken;
    }
}