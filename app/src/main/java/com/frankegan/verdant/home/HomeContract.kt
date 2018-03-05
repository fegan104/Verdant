package com.frankegan.verdant.home

import com.frankegan.verdant.models.ImgurImage

/**
 * Created by frankegan on 5/10/16.
 *
 *
 * This specifies the contract between the view and the presenter.
 */
class HomeContract {

    interface View {

        fun setProgressIndicator(active: Boolean)

        fun showImages(images: List<ImgurImage>)

        fun showImageDetailUi(image: ImgurImage, view: android.view.View)

        fun showSubredditChooser()

        fun clearImages()

        fun showBottomSheet(show: Boolean)

        fun refreshRecents()

        fun setToolbarTitle(title: String)
    }

    internal interface UserActionsListener {

        val subreddit: String

        fun loadMoreImages(page: Int)

        fun changeSubreddit(subName: String)

    }
}
