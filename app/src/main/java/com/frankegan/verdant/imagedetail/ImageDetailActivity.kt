package com.frankegan.verdant.imagedetail

import android.Manifest
import android.animation.AnimatorInflater
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewTreeObserver
import androidx.animation.addListener
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import com.frankegan.verdant.ImgurAPI
import com.frankegan.verdant.R
import com.frankegan.verdant.fullscreenimage.FullscreenImageActivity
import com.frankegan.verdant.models.ImgurImage
import com.frankegan.verdant.utils.lollipop
import com.frankegan.verdant.utils.prelollipop
import com.liuguangqiang.swipeback.SwipeBackActivity
import com.liuguangqiang.swipeback.SwipeBackLayout
import kotlinx.android.synthetic.main.image_detail_activity.*

class ImageDetailActivity : SwipeBackActivity(), ImageDetailContract.View {

    private lateinit var imgurModel : ImgurImage

    /**
     * The presenter for our [com.frankegan.verdant.imagedetail.ImageDetailContract.View].
     */
    private lateinit var actionListener: ImageDetailContract.UserActionsListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_detail_activity)
        setDragEdge(SwipeBackLayout.DragEdge.LEFT)

        //init Views
        imgurModel = intent.getParcelableExtra(IMAGE_DETAIL_EXTRA)

        detailImage.setOnClickListener { showFullscreenImage(imgurModel) }
        downloadText.setOnClickListener { tryDownload() }
        shareText.setOnClickListener { shareImage() }

        lollipop {
            val scale = AnimatorInflater.loadAnimator(this, R.animator.fab_scale_up)
            scale.start()
            //textViews
            titleText.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
            shareText.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
            viewCountText.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        }

        fab.setOnClickListener { actionListener.toggleFavoriteImage() }

        //instantiate presenter
        actionListener = ImageDetailPresenter(this, imgurModel)
        actionListener.openImage()

        //used to make transitions smooth
        supportPostponeEnterTransition()
    }

    @TargetApi(21)
    override fun onBackPressed() {
        lollipop {
            val scale = AnimatorInflater.loadAnimator(this, R.animator.fab_scale_down)
            scale.addListener(onEnd = {
                fab.visibility = View.INVISIBLE
                finishAfterTransition()
            })
            scale.start()
        }

        prelollipop { super.onBackPressed() }
    }

    /**
     * Sets the title text for the activity
     *
     * @param titleText The string to be displayed
     */
    override fun setTitle(title: String) {
        titleText.text = title
    }

    /**
     * @param descriptionText The string to be displayed.
     */
    override fun setDescription(description: String) {
        descriptionText.visibility = View.VISIBLE
        finalDescription.visibility = View.VISIBLE
        descriptionText.text = description
    }

    override fun setViewCount(views: Int) {
        viewCountText.text = views.toString()
    }

    override fun hideDescription() {
        descriptionText.visibility = View.GONE
        finalDescription.visibility = View.GONE
    }

    override fun toggleFAB() {
        fab.toggle()
        fab.jumpDrawablesToCurrentState()

        val msg = if (fab.isChecked) "Favorited  ❤️" else "Unfavorited </3"

        Snackbar.make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun checkFAB(check: Boolean) {
        fab.isChecked = check
        fab.jumpDrawablesToCurrentState()
    }

    override fun showError(error: VolleyError) {
        when (error) {
            is NoConnectionError -> Snackbar.make(findViewById(R.id.coordinator),
                    "Check your connection", Snackbar.LENGTH_SHORT).show()
            is AuthFailureError -> Snackbar.make(findViewById(R.id.coordinator),
                    "Please login", Snackbar.LENGTH_LONG)
                    .setAction("LOGIN") { ImgurAPI.login(this@ImageDetailActivity, null) }
                    .show()
            else -> Snackbar.make(findViewById(R.id.coordinator),
                    error.localizedMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showFullscreenImage(image: ImgurImage) {
        val intent = Intent(this, FullscreenImageActivity::class.java)
        intent.putExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA, image)

        val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, detailImage,
                        this.getString(R.string.image_transition_name))

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    /**
     * A method for setting the main image to be displayed in the activity
     *
     * @param link The URL of the image
     */
    override fun setImage(link: String) {
        Glide.with(this)
                .load(link)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .fitCenter()
                .into(object : GlideDrawableImageViewTarget(detailImage) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>?) {
                        super.onResourceReady(resource, animation)
                        scheduleStartPostponedTransition(detailImage)
                    }
                })
    }

    override fun shareImage() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, imgurModel.thumbLink)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    /**
     * {@inheritDoc}
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Do the stuff that requires permission...
                actionListener.downloadImage()
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show permission explanation dialog...
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("We can't save pictures to your gallery without permission.")
                    builder.show()
                } else {
                    //Never ask again selected, or device policy prohibits the app from having that permission.
                    //So, disable that feature, or fall back to another situation...
                }
            }
        }
    }

    /**
     * We need to check if we have permission to save the image since sdk 23. If we are given
     * permission then we download the image else we tell the user that we were denied permission.
     *
     * The rest of the magic happens in [.onRequestPermissionsResult].
     */
    internal fun tryDownload() {
        //check if we already have permission
        val hasPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //if we don't we need to ask for it
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    SAVE_PERMISSION)
        } else {
            actionListener.downloadImage()
        }
    }

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     *
     * @param sharedElement The view that will be animated.
     */
    internal fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        sharedElement.viewTreeObserver.removeOnPreDrawListener(this)
                        supportStartPostponedEnterTransition()
                        return true
                    }
                })
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
