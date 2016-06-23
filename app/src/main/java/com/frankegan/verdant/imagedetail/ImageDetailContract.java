package com.frankegan.verdant.imagedetail;

import android.content.Intent;

import com.android.volley.VolleyError;
import com.frankegan.verdant.models.ImgurImage;
import com.frankegan.verdant.models.RedditComment;

import java.util.List;

/**
 * Created by frankegan on 5/14/16.
 */
public interface ImageDetailContract {

    interface View {

        void setTitle(String title);

        void setImage(String imageUrl);

        void setDescription(String description);

        void setViews(int views);

        void hideDescription();

        void toggleFAB();

        void checkFAB(boolean check);

        void showError(VolleyError error);

        void showShareDialog(Intent shareIntent);

        void showFullscreenImage(ImgurImage image, android.view.View view);

        void showComments(List<RedditComment> comments);
    }

    interface UserActionsListener {

        void openImage();

        void openFullscreenImage(android.view.View view);

        void toggleFavoriteImage();

        void shareImage();

        void downloadImage();

        void loadComments();
    }
}
