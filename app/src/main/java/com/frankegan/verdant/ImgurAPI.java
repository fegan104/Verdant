package com.frankegan.verdant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.frankegan.verdant.customtabs.CustomTabActivityHelper;
import com.frankegan.verdant.models.ImgurUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
    /**
     * The URL that we call inorder to authenticate.
     */
    public static final String LOGIN_URL = "https://api.imgur.com/oauth2/authorize?client_id="
            + ImgurAPI.IMGUR_CLIENT_ID
            + "&response_type=token";

    /**
     * A callback for communicatin to presenters.
     */
    interface ImagesServiceCallback<T> {

        void onLoaded(T notes);
    }

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves data from a access token request. Saves Refresh Token, Access Token, expiration time, token type, and account User name.
     *
     * @param user The access user we're saving.
     */
    public static void saveResponse(ImgurUser user) {
        Context context = VerdantApp.getContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit().clear().apply();
        prefs.edit()
                .putString("access_token", user.getAccessToken())
                .putString("refresh_token", user.getRefreshToken())
                .putLong("expires_in", user.getExpiresIn())
                .putString("account_username", user.getAccountUsername())
                .apply();
    }

    /**
     * Access tokens expire from Imgur after a month so we have to be able to refresh them.
     *
     * @return the new access token.
     */
    public String refreshAccessToken() {
        // TODO: 5/7/16 refresh tokens once a month
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
                /*if (ImgurAPI.getInstance().isLoggedIn()) {
                    params.put("Authorization", "Bearer " +
                            VerdantApp.getContext().getSharedPreferences(ImgurAPI.PREFS_NAME, 0)
                                    .getString("access_token", null));
                } else*/
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

    /**
     * Calling this method will initiate a login flow hat end with the user either logging in or declining.
     *
     * @param host    The host activity you are calling from.
     * @param session The CustomTabSession, this is only useful you were planning on warming up tab or something like that.
     */
    public static void login(Activity host, @Nullable CustomTabsSession session) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder(session)
                .setToolbarColor(ContextCompat.getColor(host, R.color.material_lightgreen500))
                .build();

        CustomTabActivityHelper.openCustomTab(host,
                customTabsIntent,
                Uri.parse(LOGIN_URL));
    }

    /**
     * Default subbredit name because it's kinda pretty.
     */
    public static String getDefaultSubreddit(){
        Context context = VerdantApp.getContext();
        String def = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("default_sub", context.getString(R.string.itap_sub));
        Log.d(TAG, "getDefaultSubreddit: default = " + def);
        return def;
    }

    /**
     * @return The account name of the current user or null if not logged in.
     */
    public static String getAccountName() {
        return VerdantApp
                .getContext()
                .getSharedPreferences(ImgurAPI.PREFS_NAME, Context.MODE_PRIVATE)
                .getString("account_username", null);
    }
}