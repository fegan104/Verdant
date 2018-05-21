package com.frankegan.verdant.imagedetail

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnPreDraw
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.frankegan.verdant.R
import com.frankegan.verdant.SingleLiveEvent
import com.frankegan.verdant.data.ImgurImage
import com.frankegan.verdant.fullscreenimage.FullscreenImageActivity
import com.frankegan.verdant.utils.*
import com.liuguangqiang.swipeback.SwipeBackActivity
import com.liuguangqiang.swipeback.SwipeBackLayout
import kotlinx.android.synthetic.main.image_detail_activity.*

class ImageDetailActivity : SwipeBackActivity() {

    private lateinit var viewModel: ImageDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_detail_activity)
        setDragEdge(SwipeBackLayout.DragEdge.LEFT)

        viewModel = obtainViewModel(ImageDetailViewModel::class.java).apply {
            imageLiveData.value = intent.getParcelableExtra(IMAGE_DETAIL_EXTRA)
            subscribe(this@ImageDetailActivity::render)
        }

        lollipop {
            AnimUtils.animateScaleUp(fab)
            //textViews
            titleText.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
            shareText.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
            viewCountText.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        }

        //used to make transitions smooth
        supportPostponeEnterTransition()
    }

    private fun render(model: LiveData<ImgurImage>, snackbarMsg: SingleLiveEvent<String>) {
        model.observe(this) { img ->
            titleText.text = img?.title!!
            viewCountText.text = img.views.toString()
            checkFAB(img.favorite)
            setImage(img.bigThumbLink)
            if (img.description == null) {
                hideDescription()
            } else {
                setDescription(img.description)
            }

            shareText.setOnClickListener { shareImage(img) }
            downloadText.setOnClickListener { tryDownload() }
            fab.setOnClickListener { viewModel.toggleFavoriteImage(img) }
            detailImage.setOnClickListener { showFullscreenImage(img) }
        }
        snackbarMsg.observe(this) {
            coordinator.showSnackbar(it ?: "")
        }
    }

    @TargetApi(21)
    override fun onBackPressed() {
        lollipop {
            AnimUtils.animateScaleDown(fab).doOnEnd { finishAfterTransition() }
        }

        prelollipop { super.onBackPressed() }
    }

    /**
     * @param descriptionText The string to be displayed.
     */
    private fun setDescription(description: String) {
        descriptionText.visibility = View.VISIBLE
        finalDescription.visibility = View.VISIBLE
        descriptionText.text = description
    }

    private fun hideDescription() {
        descriptionText.visibility = View.GONE
        finalDescription.visibility = View.GONE
    }

    private fun checkFAB(check: Boolean) {
        fab.isChecked = check
        fab.jumpDrawablesToCurrentState()
    }

    fun showError(error: Error) {
        when (error) {
            is NoConnectionError -> Snackbar.make(findViewById(R.id.coordinator),
                    "Check your connection", Snackbar.LENGTH_SHORT).show()
            is AuthFailureError -> Snackbar.make(findViewById(R.id.coordinator),
                    "Please login", Snackbar.LENGTH_LONG)
                    .setAction("LOGIN") { ImgurAPI.login(this@ImageDetailActivity, null) }
                    .show()
            else -> Snackbar.make(findViewById(R.id.coordinator),
                    error.message ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showFullscreenImage(image: ImgurImage) {
        val intent = Intent(this, FullscreenImageActivity::class.java).apply {
            putExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA, image)
        }

        val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, detailImage, this.getString(R.string.image_transition_name))

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    /**
     * A method for setting the main image to be displayed in the activity
     *
     * @param link The URL of the image
     */
    private fun setImage(link: String) {
        Glide.with(this)
                .load(link)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .priority(Priority.IMMEDIATE)
                        .fitCenter())
                .into(object : DrawableImageViewTarget(detailImage) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        super.onResourceReady(resource, transition)
                        detailImage.doOnPreDraw { supportStartPostponedEnterTransition() }
                    }
                })
    }

    private fun shareImage(img: ImgurImage) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, img.link)
            type = "text/plain"
        }
        startActivity(sendIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) return
        val successful = grantResults[0] == PackageManager.PERMISSION_GRANTED

        if (successful) {
            //Do the stuff that requires permission...
            tryDownload()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Show permission explanation dialog...
            AlertDialog.Builder(this)
                    .setMessage(R.string.permission_request_title)
                    .setNegativeButton("Deny") {_, _ -> }
                    .setPositiveButton("Allow") {_, _ -> tryDownload()}
                    .show()
        }
    }

    /**
     * We need to check if we have permission to save the image since sdk 23. If we are given
     * permission then we download the image else we tell the user that we were denied permission.
     *
     * The rest of the magic happens in [.onRequestPermissionsResult].
     */
    private fun tryDownload() {
        //check if we already have permission
        val hasPermission = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //if we don't we need to ask for it
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    SAVE_PERMISSION)
        } else {
            viewModel.downloadImage()
        }
    }

    companion object {
        /**
         * Used for passing intents to this activity.
         */
        val IMAGE_DETAIL_EXTRA = "EXTRA.IMAGE_DETAIL"
        /**
         * Used for passing intents to this activity.
         */
        val SAVE_PERMISSION = 200
    }
}
