package com.frankegan.verdant.activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.frankegan.verdant.FABToggle;
import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.R;
import com.frankegan.verdant.VerdantApp;
import com.frankegan.verdant.models.ImgurImage;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImageDetailActivity extends SwipeBackActivity {

    final String ACCESS_TOKEN = "access_token";
    String TAG = ImageDetailActivity.class.getSimpleName();
    final static int MAX_IMAGE_SIZE = 1080;
    final public static String IMAGE_DETAIL_EXTRA = "EXTRA.IMAGE_DETAIL";
    ImageView imageView;
    FABToggle fab;
    TextView description, title;
    ImgurImage imgurImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_activity);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        imgurImage = getIntent().getParcelableExtra(IMAGE_DETAIL_EXTRA);

        imageView = (ImageView) findViewById(R.id.big_net_img);

        setImage(imgurImage.largeThumbLink);// Loads a large enough image to be in HD

        description = (TextView) findViewById(R.id.desc_text);
        setDescription(imgurImage.description);

        title = (TextView) findViewById(R.id.big_title);
        setTitle(imgurImage.title);

        fab = (FABToggle) findViewById(R.id.fab);
        fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_scale_up));
        fab.setOnClickListener((View view) -> toggleFavoriteImage(imgurImage.id));
        checkFavorite();//called in case imgurImage was already favorited

        supportPostponeEnterTransition();
    }

    @Override
    public void onBackPressed() {
        Animation scale = AnimationUtils.loadAnimation(this, R.anim.fab_scale_down);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.INVISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();
                else finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(scale);
    }


    /**
     * Sets the title text for the activity
     *
     * @param titleText The string to be displayed
     */
    public void setTitle(String titleText) {
        title.setText(titleText);
    }

    /**
     * Sets the description text
     *
     * @param descriptionText The string to be displayed
     */
    public void setDescription(String descriptionText) {
        description.setText(descriptionText);
    }

    /**
     * Logs and favorites the FAB if the image with the provided id is favorited.
     */
    public void checkFavorite() {
        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET,
                "https://api.imgur.com/3/image/" + imgurImage.id,
                null,
                (JSONObject jo) -> {
                    try {
                        Boolean fav = jo.getJSONObject("data").getBoolean("favorite");
                        fab.setChecked(fav);
                        fab.jumpDrawablesToCurrentState();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (VolleyError e) -> Log.e(TAG, e.toString())) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " +
                        getSharedPreferences(ImgurAPI.PREFS_NAME, 0)
                                .getString(ACCESS_TOKEN, null));
                return params;
            }
        };
        VerdantApp.getVolleyRequestQueue().add(jr);
    }

    /**
     * A method for favoriting the given image.(only works if user is signed in otherwise no result)
     *
     * @param id The image URL to be favorited.
     */
    public void toggleFavoriteImage(String id) {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST,
                "https://api.imgur.com/3/image/" + id + "/favorite",
                null,
                (JSONObject r) -> {
                    Toast.makeText(this, "Favorited <3", Toast.LENGTH_SHORT).show();
                    fab.toggle();
                    fab.jumpDrawablesToCurrentState();
                },
                (VolleyError e) -> {
                    //TODO make a login action on the snackbar or implement like Plaid
                    if (e instanceof NoConnectionError)
                        Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
                    else if (e instanceof AuthFailureError)
                        Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " +
                        getSharedPreferences(ImgurAPI.PREFS_NAME, MODE_PRIVATE)
                                .getString(ACCESS_TOKEN, null));
                return params;
            }
        };
        VerdantApp.getVolleyRequestQueue().add(jor);
    }

    /**
     * A method for setting the main image to be displayed in the activity
     *
     * @param link The URL of the image
     */
    public void setImage(String link) {

        Glide.with(this)
                .load(link)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        scheduleStartPostponedTransition(imageView);
                    }
                });
    }

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     */
    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                });
    }
}
