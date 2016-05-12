package com.frankegan.verdant.home;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.models.ImgurImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frankegan on 5/10/16.
 */
public class HomePresenter implements HomeContract.UserActionsListener{

    private final HomeContract.View homeView;

    /**
     * The presenter middleman between View and Model.
     *
     * @param homeView The view we are presenting.
     */
    public HomePresenter(HomeContract.View homeView) {
        this.homeView = homeView;
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
                e -> Log.e(getClass().getSimpleName(), e.toString()),
                ImgurAPI.DEFAULT,
                newPage);

    }

    @Override
    public void openImageDetails(@NonNull ImgurImage requestedImage, View v) {
        homeView.showImageDetailUi(requestedImage, v);
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
                        responseObj.get("id").toString(),
                        responseObj.get("title").toString(),
                        responseObj.get("description").toString(),
                        responseObj.getBoolean("favorite"));
                images.add(datum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return images;
    }
}
