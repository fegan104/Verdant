package com.frankegan.verdant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.frankegan.verdant.customtabs.CustomTabActivityHelper;
import com.frankegan.verdant.models.ImgurUser;
import com.pixplicity.easyprefs.library.Prefs;

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
     * Loads a page of photos.
     * <p>
     *
     * @param success   The success response handler.
     * @param error     The error response handler.
     * @param subreddit The subreddit we want to laod for.
     * @param newPage   The page we want to load.
     */
    public static void loadPage(Response.Listener<JSONObject> success,
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
    public static String getURLForSubredditPage(String subreddit, int i) {
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
        String def = Prefs.getString("default_sub", context.getString(R.string.itap_sub));
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