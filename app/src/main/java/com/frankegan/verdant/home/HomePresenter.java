package com.frankegan.verdant.home;

import android.support.annotation.NonNull;
import android.view.View;

import com.android.volley.VolleyError;
import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.models.ImgurImage;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by frankegan on 5/10/16.
 */
public class HomePresenter implements HomeContract.UserActionsListener{

    private final HomeContract.View homeView;
    private String subName;

    /**
     * The presenter middleman between View and Model.
     *
     * @param subName The name of the subreddit we're modeling.
     * @param homeView The {@link com.frankegan.verdant.home.HomeContract.View} we are presenting.
     */
    public HomePresenter(String subName, HomeContract.View homeView) {
        this.homeView = homeView;
        this.subName = subName;
    }

    /**
     * Loads the next page of images as user scrolls.
     *
     * @param newPage page to be loaded, starts at 0.
     */
    @Override
    public void loadMoreImages(int newPage) {
        homeView.setProgressIndicator(true);
        ImgurAPI.getInstance().loadPage(
                r -> {
                    homeView.showImages(jsonToList(r));
                    homeView.setProgressIndicator(false);
                },
                VolleyError::printStackTrace,
                subName,
                newPage);

    }

    @Override
    public void openImageDetails(@NonNull ImgurImage requestedImage, View v) {
        homeView.showBottomSheet(false);
        homeView.showImageDetailUi(requestedImage, v);
    }

    @Override
    public void changeSubreddit(String subName) {
        homeView.showBottomSheet(false);
        //recover list
        List<String> recents = new ArrayList<>(Prefs.getStringSet("recent_subreddits", new HashSet<>()));
        recents.add(0, subName);
        //save edited list
        Prefs.putStringSet("recent_subreddits", new HashSet<>(recents));
        //update changes
        homeView.refreshRecents();
        homeView.clearImages();
        this.subName = subName;
        loadMoreImages(0);
    }

    /**
     * Converts a {@link JSONObject} to a {@link List} of {@link ImgurImage}s.
     *
     * @param object The response object from Imgur.
     * @return a list of parsed {@link ImgurImage}s.
     */
    List<ImgurImage> jsonToList(JSONObject object) {
        List<ImgurImage> images = new ArrayList<>();
        JSONArray responseJSONArray;
        try {
            responseJSONArray = object.getJSONArray("data");
            for (int i = 0; i < responseJSONArray.length(); i++) {
                JSONObject responseObj = responseJSONArray.getJSONObject(i);
                ImgurImage datum = new ImgurImage(
                        responseObj.getString("id"),
                        responseObj.getString("title"),
                        responseObj.getString("description"),
                        responseObj.getBoolean("favorite"),
                        responseObj.getBoolean("animated"),
                        responseObj.getInt("views"));
                images.add(datum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return images;
    }
}
