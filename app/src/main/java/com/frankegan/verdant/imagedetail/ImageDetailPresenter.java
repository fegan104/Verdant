package com.frankegan.verdant.imagedetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.VerdantApp;
import com.frankegan.verdant.models.ImgurImage;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frankegan on 5/14/16.
 */
public class ImageDetailPresenter implements ImageDetailContract.UserActionsListener {
    /**
     * The {@link com.frankegan.verdant.imagedetail.ImageDetailContract.View} we're presenting.
     */
    private final ImageDetailContract.View detailView;
    /**
     * The {@link ImgurImage} we're modelling.
     */
    private ImgurImage model;
    /**
     * Used to make sure we don't misspell "accces_tokn".
     */
    private final String ACCESS_TOKEN = "access_token";

    public ImageDetailPresenter(ImageDetailContract.View detailView, ImgurImage model) {
        this.detailView = detailView;
        this.model = model;
    }

    @Override
    public void openImage(@NonNull ImgurImage image) {
        detailView.setImage(model.getLargeThumbnailLink());
        detailView.setTitle(model.getTitle());
        checkFavoriteImage(model);
        if(model.getDescription().equals("null"))
            detailView.hideDescription();
        else detailView.setDescription(model.getDescription());
    }

    @Override
    public void openFullscreenImage(View view) {
        detailView.showFullscreenImage(model, view);
    }

    @Override
    public void toggleFavoriteImage(ImgurImage image) {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST,
                "https://api.imgur.com/3/image/" + image.getId() + "/favorite",
                null,
                r -> detailView.toggleFAB(),
                e -> detailView.showError(e)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " +
                        VerdantApp.getContext().getSharedPreferences(ImgurAPI.PREFS_NAME, Context.MODE_PRIVATE)
                                .getString(ACCESS_TOKEN, null));
                return params;
            }
        };
        VerdantApp.getVolleyRequestQueue().add(jor);
    }

    /**
     * Sets proper FAB toggle state during opening.
     * @param image The image we're checking the state of.
     */
    void checkFavoriteImage(ImgurImage image) {
        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET,
                "https://api.imgur.com/3/image/" + image.getId(),
                null,
                jo -> {
                    try {
                        Boolean fav = jo.getJSONObject("data").getBoolean("favorite");
                        detailView.checkFAB(fav);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                VolleyError::printStackTrace) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " +
                        VerdantApp.getContext().getSharedPreferences(ImgurAPI.PREFS_NAME, 0)
                                .getString(ACCESS_TOKEN, null));
                return params;
            }
        };
        VerdantApp.getVolleyRequestQueue().add(jr);
    }
}
