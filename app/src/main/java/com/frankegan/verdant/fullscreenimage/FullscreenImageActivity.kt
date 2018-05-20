package com.frankegan.verdant.fullscreenimage

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import androidx.core.view.doOnPreDraw
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.frankegan.verdant.R
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.imagedetail.ImageDetailActivity
import com.frankegan.verdant.utils.OptimisticRequestListener
import kotlinx.android.synthetic.main.activity_fullscreen_image.*

class FullscreenImageActivity : AppCompatActivity() {

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
        imageModel.run {
            if (animated) {
                setGif(link)
            } else {
                setImage(link)
            }
        }
        //used to make transitions smooth
        supportPostponeEnterTransition()
    }

    private fun setGif(link: String) {
        subsamplingScaleImageView.visibility = View.GONE
        imageView.doOnPreDraw { supportStartPostponedEnterTransition() }
        Glide.with(this)
                .load(link)
                .apply(options)
                .listener(OptimisticRequestListener<Drawable> {
                    progressBar.visibility = View.GONE
                })
                .into(imageView)
    }

    private fun setImage(link: String) {
        imageView.visibility = View.GONE
        Glide.with(this)
                .asBitmap()
                .load(link)
                .apply(options)
                .into(object : SimpleTarget<Bitmap>(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        subsamplingScaleImageView.apply {
                            setImage(ImageSource.bitmap(resource))
                            doOnPreDraw { supportStartPostponedEnterTransition() }
                        }
                        progressBar.visibility = View.GONE
                    }
                })
    }

    override fun onBackPressed() {
        subsamplingScaleImageView.resetScaleAndCenter()
        super.onBackPressed()
    }

    companion object {
        const val MAX_IMAGE_SIZE = 2048
    }
}
