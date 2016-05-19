package com.frankegan.verdant.fullscreenimage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.frankegan.verdant.R;
import com.frankegan.verdant.imagedetail.ImageDetailActivity;
import com.frankegan.verdant.models.ImgurImage;

public class FullscreenImageActivity extends AppCompatActivity implements FullscreenImageContract.View {

    /**
     * The imageView in focus for our full screen content.
     */
    SubsamplingScaleImageView imageView;
    /**
     * model from intent.
     */
    ImgurImage imageModel;
    FullscreenImageContract.UserActionsListener actionListener;

    public static final int MAX_IMAGE_SIZE = 2048;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        //init Views
        imageView = (SubsamplingScaleImageView) findViewById(R.id.fullscreen_imageview);

        imageModel = getIntent().getParcelableExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA);
        actionListener = new FullscreenImagePresenter(imageModel, this);

        //used to make transitions smooth
        supportPostponeEnterTransition();
    }

    @Override
    public void setProgressIndicator(boolean active) {
        // TODO: 5/18/16 add progress bar or remove method
    }

    @Override
    public void setImage(String link) {
        Glide.with(this)
                .load(link)
                .asBitmap()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .into(new SimpleTarget<Bitmap>(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImage(ImageSource.bitmap(resource));
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

    @Override
    public void onBackPressed() {
        imageView.resetScaleAndCenter();
        super.onBackPressed();
    }
}
