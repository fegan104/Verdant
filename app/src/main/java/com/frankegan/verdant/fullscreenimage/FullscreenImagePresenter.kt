package com.frankegan.verdant.fullscreenimage

import com.frankegan.verdant.models.ImgurImage

/**
 * Created by frankegan on 5/18/16.
 */
class FullscreenImagePresenter(internal var imageModel: ImgurImage,
                               internal var fullscreenView: FullscreenImageContract.View) :
        FullscreenImageContract.UserActionsListener {

    init {
        if (imageModel.animated) {
            fullscreenView.setGif(imageModel.thumbLink)
        } else {
            fullscreenView.setImage(imageModel.thumbLink)
        }
    }

    override fun lightsOut() {
        // TODO: 5/18/16 look up how to go lights out
    }
}
