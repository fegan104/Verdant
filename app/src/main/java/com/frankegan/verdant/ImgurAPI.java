package com.frankegan.verdant;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankegan created on 10/24/15.
 */
public class ImgurAPI {

    private static final String TAG = ImgurAPI.class.getSimpleName();

    private static ImgurAPI INSTANCE;

    public static final String SHARED_PREFERENCES_NAME = "imgur_auth";

    public static final String IMGUR_CLIENT_ID = BuildConfig.IMGUR_CLIENT_ID;
    public static final String IMGUR_CLIENT_SECRET = BuildConfig.IMGUR_CLIENT_SECRET;
    public static final String IMGUR_REDIRECT_URL = "http://android";

    private ImgurAPI() {//privated to assure use of getInstance
    }

    public static ImgurAPI getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ImgurAPI();
        return INSTANCE;
    }

    public boolean isLoggedIn() {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
        return !TextUtils.isEmpty(prefs.getString("refresh_token", null));
    }

    public void saveRefreshToken(String refresh, String access, long expires) {
        Context context = VerdantApp.getContext();
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0)
                .edit()
                .putString("access_token", access)
                .putString("refresh_token", refresh)
                .putLong("expires_in", expires)
                .apply();
    }

    public void saveRefreshTokenFromJSON(JSONObject json) {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
        try {
            String accessToken = json.getString("access_token");
            String newRefreshToken = json.getString("refresh_token");
            long expiresIn = json.getLong("expires_in");
            String tokenType = json.getString("token_type");
            String accountUsername = json.getString("account_username");

            prefs.edit()
                    .putString("access_token", accessToken)
                    .putString("refresh_token", newRefreshToken)
                    .putLong("expires_in", expiresIn)
                    .putString("token_type", tokenType)
                    .putString("account_username", accountUsername)
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String requestNewAccessTokenWithRefresh() {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
        String refreshToken = prefs.getString("refresh_token", null);

        if (refreshToken == null) {
            Log.w(TAG, "refresh token is null; cannot request access token. login first.");
            return null;
        }

        // clear previous access token
        prefs.edit().remove("access_token").apply();
        Response.Listener<JSONObject> successResponse = (JSONObject response) -> {
            Log.d("frankegan", "RefreshToken successfully traded for Token");
            Log.d("frankegan", "Response = " + response);
            saveRefreshTokenFromJSON(response);
        };

        JsonObjectRequest sr = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.imgur.com/oauth2/token/",
                null,
                successResponse,
                (VolleyError error) -> Log.e("volley", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("refresh_token", refreshToken);
                params.put("client_id", ImgurAPI.IMGUR_CLIENT_ID);
                params.put("client_secret", ImgurAPI.IMGUR_CLIENT_SECRET);
                params.put("grant_type", "refresh_token");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Client-ID " + ImgurAPI.IMGUR_CLIENT_ID);
                return params;
            }
        };
        VerdantApp.getVolleyRequestQueue().add(sr);

        return prefs.getString("access_token", null);
    }

    public String requestTokenWithPin(String pin) {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);

        // clear previous access token
        prefs.edit().remove("access_token").apply();

        Response.Listener<String> successResponse = (String response) -> {
            Log.i("frankegan", "Pin Successfully traded for Token");
            Log.i("frankegan", "Response = " + response);

            try {
                JSONObject root = new JSONObject(response);
                String accessToken = root.getString("access_token");
                String refreshToken = root.getString("refresh_token");
                long expiresIn = root.getLong("expires_in");
                String tokenType = root.getString("token_type");
                String accountUsername = root.getString("account_username");

                prefs.edit()
                        .putString("access_token", accessToken)
                        .putString("refresh_token", refreshToken)
                        .putLong("expires_in", expiresIn)
                        .putString("token_type", tokenType)
                        .putString("account_username", accountUsername)
                        .commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        StringRequest sr = new StringRequest(Request.Method.POST,
                "https://api.imgur.com/oauth2/token/",
                successResponse,
                (VolleyError error) -> Log.e("volley", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_id", ImgurAPI.IMGUR_CLIENT_ID);
                params.put("client_secret", ImgurAPI.IMGUR_CLIENT_SECRET);
                params.put("grant_type", "pin");
                params.put("pin", pin);
                Log.i(TAG, "Params = " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Client-ID " + ImgurAPI.IMGUR_CLIENT_ID);
                return params;
            }
        };
        VerdantApp.getVolleyRequestQueue().add(sr);

        return prefs.getString("access_token", null);
    }

    public void loadPage(Response.Listener<JSONObject> success,
                         Response.ErrorListener error,
                         String subreddit,
                         int newPage) {
        //Our request complete with headers
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                Request.Method.GET,
                getURLForSubredditPage(subreddit, newPage),
                null,
                success,
                error) {

            @Override
            public HashMap<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                if (ImgurAPI.getInstance().isLoggedIn()) {
                    params.put("Authorization", "Bearer " +
                            VerdantApp.getContext().getSharedPreferences(ImgurAPI.SHARED_PREFERENCES_NAME, 0)
                                    .getString("access_token", null));
                } else
                    params.put("Authorization", "Client-ID " + ImgurAPI.IMGUR_CLIENT_ID);
                return params;
            }
        };

        VerdantApp.getVolleyRequestQueue().add(jsonReq);
    }

    public void logout() {
        Context context = VerdantApp.getContext();
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0)
                .edit()
                .clear()
                .commit();
    }

    public String getURLForSubredditPage(String subreddit, int i) {
        return "https://api.imgur.com/3/gallery/r/" + subreddit + "/" + i + ".json";
    }

}