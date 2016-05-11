package com.frankegan.verdant.home;

import android.support.annotation.NonNull;

import com.frankegan.verdant.models.ImgurImage;

import java.util.List;

/**
 * Created by frankegan on 5/10/16.
 * <p>
 * This specifies the contract between the view and the presenter.
 */
public class HomeContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showImages(List<ImgurImage> notes);

        void showImageDetailUi(String noteId);
    }

    interface UserActionsListener {

        void loadMoreImages(int page);

        void openImageDetails(@NonNull ImgurImage requestedNote);
    }
}
