package com.frankegan.verdant.imagedetail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.frankegan.verdant.FABToggle;
import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.R;
import com.frankegan.verdant.fullscreenimage.FullscreenImageActivity;
import com.frankegan.verdant.models.ImgurImage;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

public class ImageDetailActivity extends SwipeBackActivity implements ImageDetailContract.View {

    /**
     * Logging TAG.
     */
    private final String TAG = ImageDetailActivity.class.getSimpleName();
    /**
     * Used to make sure we don't misspell "accces_tokn".
     */
    private final String ACCESS_TOKEN = "access_token";
    /**
     * Used for passing intents to this activity.
     */
    public final static String IMAGE_DETAIL_EXTRA = "EXTRA.IMAGE_DETAIL";
    /**
     * The main content {@link ImageView}
     */
    ImageView imageView;
    /**
     * The heart FAB for favoriting.
     */
    FABToggle fab;
    /**
     * The content for the title and description.
     */
    TextView description, title;
    /**
     * The model.
     */
    ImgurImage imgurImage;
    /**
     * The presenter for our {@link com.frankegan.verdant.imagedetail.ImageDetailContract.View}.
     */
    ImageDetailContract.UserActionsListener actionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_activity);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        //init Views
        imgurImage = getIntent().getParcelableExtra(IMAGE_DETAIL_EXTRA);
        imageView = (ImageView) findViewById(R.id.big_net_img);
        imageView.setOnClickListener((View v) -> actionListener.openFullscreenImage(v));
        description = (TextView) findViewById(R.id.desc_text);
        title = (TextView) findViewById(R.id.big_title);

        //init FAB with action listener
        fab = (FABToggle) findViewById(R.id.fab);
        fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_scale_up));
        fab.setOnClickListener(v -> actionListener.toggleFavoriteImage(imgurImage));

        //instantiate presenter
        actionListener = new ImageDetailPresenter(this, imgurImage);
        actionListener.openImage(imgurImage);

        //used to make transitions smooth
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
    @Override
    public void setTitle(String titleText) {
        title.setText(titleText);
    }

    /**
     * @param descriptionText The string to be displayed.
     */
    @Override
    public void setDescription(String descriptionText) {
        description.setVisibility(View.VISIBLE);
        description.setText(descriptionText);
    }

    @Override
    public void hideDescription() {
        description.setVisibility(View.GONE);
    }

    @Override
    public void toggleFAB() {
        fab.toggle();
        fab.jumpDrawablesToCurrentState();

        String msg;
        if (fab.isChecked())
            msg = "Favorited  ❤️";
        else msg = "Unfavorited </3";

        Snackbar.make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void checkFAB(boolean check) {
        fab.setChecked(check);
        fab.jumpDrawablesToCurrentState();
    }

    @Override
    public void showError(VolleyError e) {
        if (e instanceof NoConnectionError)
            Snackbar.make(findViewById(R.id.coordinator), "Check your connection", Snackbar.LENGTH_SHORT).show();
        else if (e instanceof AuthFailureError)
            Snackbar.make(findViewById(R.id.coordinator), "Please login", Snackbar.LENGTH_LONG)
                    .setAction("LOGIN", v -> ImgurAPI.login(ImageDetailActivity.this, null))
                    .show();
        else
            Snackbar.make(findViewById(R.id.coordinator), "Unknown error occurred", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showFullscreenImage(ImgurImage image, View view) {
        Intent intent = new Intent(this, FullscreenImageActivity.class);
        intent.putExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA, image);

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, view.findViewById(R.id.big_net_img),
                        this.getString(R.string.image_transition_name));

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    /**
     * A method for setting the main image to be displayed in the activity
     *
     * @param link The URL of the image
     */
    @Override
    public void setImage(String link) {
        Glide.with(this)
                .load(link)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .fitCenter()
                .into(new GlideDrawableImageViewTarget(imageView){
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        scheduleStartPostponedTransition(imageView);
                    }
                });
    }

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     *
     * @param sharedElement The view that will be animated.
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
