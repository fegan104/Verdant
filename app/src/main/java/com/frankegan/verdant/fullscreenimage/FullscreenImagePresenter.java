package com.frankegan.verdant.fullscreenimage;

import com.frankegan.verdant.models.ImgurImage;

/**
 * Created by frankegan on 5/18/16.
 */
public class FullscreenImagePresenter implements FullscreenImageContract.UserActionsListener {

    ImgurImage imageModel;
    FullscreenImageContract.View fullscreenView;

    public FullscreenImagePresenter(ImgurImage imageModel, FullscreenImageContract.View fullscreenView) {
        this.imageModel = imageModel;
        this.fullscreenView = fullscreenView;
        if (imageModel.getAnimated())
            fullscreenView.setGif(imageModel.getThumbLink());
        else
            fullscreenView.setImage(imageModel.getThumbLink());
    }

    @Override
    public void lightsOut() {
        // TODO: 5/18/16 look up how to go lights out
    }
}
