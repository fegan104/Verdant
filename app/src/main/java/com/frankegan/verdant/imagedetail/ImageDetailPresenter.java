package com.frankegan.verdant.imagedetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.VerdantApp;
import com.frankegan.verdant.fullscreenimage.FullscreenImageActivity;
import com.frankegan.verdant.models.ImgurImage;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public void openImage() {
        detailView.setImage(model.getLargeThumbnailLink());
        detailView.setTitle(model.getTitle());
        checkFavoriteImage(model);
        detailView.setViews(model.getViews());
        if (model.getDescription().equals("null"))
            detailView.hideDescription();
        else detailView.setDescription(model.getDescription());
    }

    @Override
    public void openFullscreenImage(View view) {
        if (PreferenceManager.getDefaultSharedPreferences(VerdantApp.getContext()).getBoolean("enable_fullscreen", false))
            detailView.showFullscreenImage(model, view);
    }

    @Override
    public void toggleFavoriteImage() {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST,
                "https://api.imgur.com/3/image/" + model.getId() + "/favorite",
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

    @Override
    public void shareImage() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, model.getLink());
        sendIntent.setType("text/plain");
        detailView.showShareDialog(sendIntent);
    }

    @Override
    public void downloadImage() {
        //download image
        Glide.
                with(VerdantApp.getContext())
                .load(model.getLink())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(FullscreenImageActivity.MAX_IMAGE_SIZE,
                        FullscreenImageActivity.MAX_IMAGE_SIZE) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //save to pictures directory on external storage
                        String filePath = Environment.
                                getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                .getAbsolutePath() + "/Verdant";
                        //make directory
                        File dir = new File(filePath);
                        dir.mkdirs();
                        //make file we're saving to
                        File file = new File(dir, model.getId() + ".png");
                        try {
                            //make sure file exists
                            file.createNewFile();
                            //open stream
                            FileOutputStream fOut = new FileOutputStream(file);
                            // Use the compress method on the BitMap object to write image to the OutputStream
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            Toast.makeText(VerdantApp.getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Sets proper FAB toggle state during opening.
     *
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

    @Override
    public void loadComments() {
        // TODO: 6/22/16 actually parse comments
        ImgurAPI.loadComments(
                r -> {
                    try {
                        Log.d(getClass().getSimpleName(), "loadComments: JSONObject = " + r.toString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    detailView.showComments(null);
                },
                VolleyError::printStackTrace,
                model.getCommentsLink());
    }
}
