package com.frankegan.verdant.fullscreenimage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        //init Views
        imageView = (SubsamplingScaleImageView) findViewById(R.id.fullscreen_imageview);

        imageModel = getIntent().getParcelableExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA);
        actionListener = new FullscreenImagePresenter(imageModel, this);
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
                .into(new SimpleTarget<Bitmap>(4096, 4096) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImage(ImageSource.bitmap(resource));
                    }
                });
    }
}
