package com.frankegan.verdant.fullscreenimage

/**
 * Created by frankegan on 5/18/16.
 */
interface FullscreenImageContract {
    interface View {

        fun setImage(link: String)

        fun setGif(link: String)
    }

    interface UserActionsListener {

        fun lightsOut()
    }
}
