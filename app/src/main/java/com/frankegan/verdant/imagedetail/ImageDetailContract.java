package com.frankegan.verdant.imagedetail;

import android.support.annotation.Nullable;

import com.android.volley.VolleyError;
import com.frankegan.verdant.models.ImgurImage;

/**
 * Created by frankegan on 5/14/16.
 */
public interface ImageDetailContract {

    interface View {

        void setTitle(String title);

        void setImage(String imageUrl);

        void setDescription(String description);

        void hideDescription();

        void toggleFAB();

        void checkFAB(boolean check);

        void showError(VolleyError error);

        void showFullscreenImage(ImgurImage image, android.view.View view);
    }

    interface UserActionsListener {

        void openImage(@Nullable ImgurImage image);

        void openFullscreenImage(android.view.View view);

        void toggleFavoriteImage(ImgurImage image);
    }
}
