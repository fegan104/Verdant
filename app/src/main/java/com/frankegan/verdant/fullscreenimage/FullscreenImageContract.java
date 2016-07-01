package com.frankegan.verdant.fullscreenimage;

/**
 * Created by frankegan on 5/18/16.
 */
public interface FullscreenImageContract {
    interface View {

        void setImage(String link);

        void setGif(String link);
    }

    interface UserActionsListener {

        void lightsOut();
    }
}
