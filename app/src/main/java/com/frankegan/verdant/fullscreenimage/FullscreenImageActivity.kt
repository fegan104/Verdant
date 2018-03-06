package com.frankegan.verdant.fullscreenimage

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.frankegan.verdant.R
import com.frankegan.verdant.imagedetail.ImageDetailActivity
import com.frankegan.verdant.models.ImgurImage
import com.frankegan.verdant.utils.OptimisticRequestListener
import kotlinx.android.synthetic.main.activity_fullscreen_image.*

class FullscreenImageActivity : AppCompatActivity(), FullscreenImageContract.View {
    /**
     * The presenter that captures our interactions.
     */
    private lateinit var actionListener: FullscreenImageContract.UserActionsListener
    private val options = RequestOptions()
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .priority(Priority.IMMEDIATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)
        //init Views
        subsamplingScaleImageView.setOnClickListener { onBackPressed() }
        imageView.setOnClickListener { onBackPressed() }
        //pass model of to presenter
        val imageModel = intent.getParcelableExtra<ImgurImage>(ImageDetailActivity.IMAGE_DETAIL_EXTRA)
        actionListener = FullscreenImagePresenter(imageModel, this)

        //used to make transitions smooth
        supportPostponeEnterTransition()
    }

    override fun setGif(link: String) {
        subsamplingScaleImageView.visibility = View.GONE
        scheduleStartPostponedTransition(imageView)
        Glide.with(this)
                .load(link)
                .apply(options)
                .listener(OptimisticRequestListener<Drawable> {
                    progressBar.visibility = View.GONE
                })
                .into(imageView)
    }

    override fun setImage(link: String) {
        imageView.visibility = View.GONE
        Glide.with(this)
                .asBitmap()
                .load(link)
                .apply(options)
                .into(object : SimpleTarget<Bitmap>(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        subsamplingScaleImageView.setImage(ImageSource.bitmap(resource))
                        progressBar.visibility = View.GONE
                        scheduleStartPostponedTransition(subsamplingScaleImageView)
                    }
                })
    }

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     *
     * @param sharedElement The view that will be animated.
     */
    private fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        sharedElement.viewTreeObserver.removeOnPreDrawListener(this)
                        supportStartPostponedEnterTransition()
                        return true
                    }
                })
    }

    override fun onBackPressed() {
        subsamplingScaleImageView.resetScaleAndCenter()
        super.onBackPressed()
    }

    companion object {
        val MAX_IMAGE_SIZE = 2048
    }
}
