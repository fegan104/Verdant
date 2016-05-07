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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankegan created on 10/24/15.
 */
public class ImgurAPI {
    /**
     * Tag for logging.
     */
    private static final String TAG = ImgurAPI.class.getSimpleName();
    /**
     * instance for singleton design pattern.
     */
    private static ImgurAPI INSTANCE;
    /**
     * Name for saving data to shared preferences.
     */
    public static final String PREFS_NAME = "imgur_auth";
    /**
     * Granted by Imgur for developing, need to make requests.
     */
    public static final String IMGUR_CLIENT_ID = BuildConfig.IMGUR_CLIENT_ID;
    /**
     * Neede to access API; granted by Imgur.
     */
    public static final String IMGUR_CLIENT_SECRET = BuildConfig.IMGUR_CLIENT_SECRET;
    /**
     * Redirect URL specified in Imgur developer console.
     */
    public static final String IMGUR_REDIRECT_URL = "verdant://logincallback";

    private ImgurAPI() {//privated to assure use of getInstance
    }

    /**
     * This is method is used in the Singleton Design Pattern.
     *
     * @return the instance of this class.
     */
    public static ImgurAPI getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ImgurAPI();
        return INSTANCE;
    }

    /**
     * Is the current user logged in?
     *
     * @return whether the user is logged in.
     */
    public boolean isLoggedIn() {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return !TextUtils.isEmpty(prefs.getString("refresh_token", null));
    }

    /**
     * Saves data from a access token request. Saves Refresh Token, Access Token, expiration time, token type, and account User name.
     *
     * @param json The response from a refresh acccess token request.
     */
    static void saveResponse(JSONObject json) {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        try {
            String accessToken = json.getString("access_token");
            String newRefreshToken = json.getString("refresh_token");
            long expiresIn = json.getLong("expires_in");
            String tokenType = json.getString("token_type");
            String accountUsername = json.getString("account_username");
            prefs.edit().clear().apply();
            prefs.edit()
                    .putString("access_token", accessToken)
                    .putString("refresh_token", newRefreshToken)
                    .putLong("expires_in", expiresIn)
                    .putString("token_type", tokenType)
                    .putString("account_username", accountUsername)
                    .apply();

        Log.d(TAG, "*********************");
        Log.d(TAG, "access_toke = " + accessToken);
        Log.d(TAG, "refresh_token = "+ newRefreshToken);
        Log.d(TAG, "account_username = " + accountUsername);
        Log.d(TAG, "*********************");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves data from a access token request. Saves Refresh Token, Access Token, expiration time, token type, and account User name.
     *
     * @param accessToken     The access token we're saving.
     * @param refreshToken    The refresh token we're saving.
     * @param expiresIn       The expiration time of the access token we're saving.
     * @param tokenType       The token type we're saving.
     * @param accountUsername The account user name of the user we're saving.
     */
    public static void saveResponse(String accessToken,
                             String refreshToken,
                             long expiresIn,
                             String tokenType,
                             String accountUsername) {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "saveResponse: access_token = " + accessToken);
        prefs.edit().clear().apply();
        prefs.edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .putLong("expires_in", expiresIn)
                .putString("token_type", tokenType)
                .putString("account_username", accountUsername)
                .apply();

        Log.d(TAG, "*********************");
        Log.d(TAG, "access_toke = " + accessToken);
        Log.d(TAG, "refresh_token = "+ refreshToken);
        Log.d(TAG, "account_username = " + accountUsername);
        Log.d(TAG, "*********************");
    }

    /**
     * Access tokens expire from Imgur after a month so we have to be able to refresh them.
     *
     * @return the new access token.
     */
    public String refreshAccessToken() {
//        Context context = VerdantApp.getContext();
//        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
//        String refreshToken = prefs.getString("refresh_token", null);
//
//        //check if we even have a refresh token
//        if (refreshToken == null) {
//            Log.w(TAG, "refresh token is null; cannot request access token. login first.");
//            return null;
//        }
//
//        // clear previous access token
//        prefs.edit().remove("access_token").apply();
//        Response.Listener<JSONObject> success = (JSONObject j) -> saveResponse(j);
//
//        //get new access token
//        JsonObjectRequest sr = new JsonObjectRequest(
//                Request.Method.POST,
//                "https://api.imgur.com/oauth2/token/",
//                null,
//                success,
//                (VolleyError error) -> Log.e(TAG, error.toString())) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("refresh_token", refreshToken);
//                params.put("client_id", ImgurAPI.IMGUR_CLIENT_ID);
//                params.put("client_secret", ImgurAPI.IMGUR_CLIENT_SECRET);
//                params.put("grant_type", "refresh_token");
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Authorization", "Client-ID " + ImgurAPI.IMGUR_CLIENT_ID);
//                return params;
//            }
//        };
//
//        //send request
//        VerdantApp.getVolleyRequestQueue().add(sr);

        return null;//prefs.getString("access_token", null);
    }

    /**
     * Requests an access token for a pin granted by Imgur.
     *
     * @param pin The pin Imgur gave our user.
     * @return The access token we got.
     */
    public String requestTokenWithPin(String pin) {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

        // clear previous access token
        prefs.edit().remove("access_token").apply();
        //create new request
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST,
                "https://api.imgur.com/oauth2/token/",
                null,
                ImgurAPI::saveResponse,
                (VolleyError error) -> Log.e("volley", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
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
        //send new request
        VerdantApp.getVolleyRequestQueue().add(sr);

        return prefs.getString("access_token", null);
    }

    /**
     * Loads a page of photos.
     * <p>
     *
     * @param success   The success response handler.
     * @param error     The error response handler.
     * @param subreddit The subreddit we want to laod for.
     * @param newPage   The page we want to load.
     */
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
                            VerdantApp.getContext().getSharedPreferences(ImgurAPI.PREFS_NAME, 0)
                                    .getString("access_token", null));
                } else
                    params.put("Authorization", "Client-ID " + ImgurAPI.IMGUR_CLIENT_ID);
                return params;
            }
        };

        VerdantApp.getVolleyRequestQueue().add(jsonReq);
    }

    /**
     * Deletes all the data we have saved for our user. This means they will have to login again to use their account.
     */
    public void logout() {
        Context context = VerdantApp.getContext();
        context.getSharedPreferences(PREFS_NAME, 0)
                .edit()
                .clear()
                .commit();
    }

    /**
     * Gets the url for making a request for a specific page of images.
     *
     * @param subreddit the subreddit we would like to request a page in.
     * @param i         The page we want.
     * @return The URL for a page of photos in a subreddit.
     */
    public String getURLForSubredditPage(String subreddit, int i) {
        return "https://api.imgur.com/3/gallery/r/" + subreddit + "/" + i + ".json";
    }

}