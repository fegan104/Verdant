package com.frankegan.verdant.imagedetail

import com.android.volley.VolleyError
import com.frankegan.verdant.models.ImgurImage

/**
 * Created by frankegan on 5/14/16.
 */
interface ImageDetailContract {

    interface View {

        fun setTitle(title: String)

        fun setImage(link: String)

        fun setDescription(description: String)

        fun setViewCount(views: Int)

        fun hideDescription()

        fun toggleFAB()

        fun checkFAB(check: Boolean)

        fun showError(error: VolleyError)

        fun shareImage()

        fun showFullscreenImage(image: ImgurImage)
    }

    interface UserActionsListener {

        fun openImage()

        fun toggleFavoriteImage()

        fun downloadImage()
    }
}
