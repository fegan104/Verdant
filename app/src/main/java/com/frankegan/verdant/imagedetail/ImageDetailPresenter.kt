package com.frankegan.verdant.imagedetail


import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.frankegan.verdant.ImgurAPI
import com.frankegan.verdant.VerdantApp
import com.frankegan.verdant.fullscreenimage.FullscreenImageActivity
import com.frankegan.verdant.models.ImgurImage
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Created by frankegan on 5/14/16.
 */
class ImageDetailPresenter(
        private val detailView: ImageDetailContract.View, private val model: ImgurImage)
    : ImageDetailContract.UserActionsListener {
    /**
     * Used to make sure we don't misspell "accces_tokn".
     */
    private val ACCESSTOKEN = "access_token"

    override fun openImage() {
        detailView.setImage(model.bigThumbLink)
        detailView.setTitle(model.title)
        checkFavoriteImage(model)
        detailView.setViewCount(model.views)
        if (model.description == "null") {
            detailView.hideDescription()
        } else {
            detailView.setDescription(model.description)
        }
    }

    override fun toggleFavoriteImage() {
        val jor = object : JsonObjectRequest(Request.Method.POST,
                "https://api.imgur.com/3/image/" + model.id + "/favorite", null,
                { detailView.toggleFAB() },
                { detailView.showError(it) }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] =
                        "Bearer ${VerdantApp.instance
                                .getSharedPreferences(ImgurAPI.PREFS_NAME, Context.MODE_PRIVATE)
                                .getString(ACCESSTOKEN, "")}"
                return params
            }
        }
        VerdantApp.volleyRequestQueue.add<JSONObject>(jor)
    }

    override fun downloadImage() {
        //download image
        Glide.with(VerdantApp.instance)
                .asBitmap()
                .load(model.thumbLink)
                .into(object : SimpleTarget<Bitmap>(FullscreenImageActivity.MAX_IMAGE_SIZE,
                        FullscreenImageActivity.MAX_IMAGE_SIZE) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        //save to pictures directory on external storage
                        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                .absolutePath + "/Verdant"
                        //make directory
                        val dir = File(filePath)
                        dir.mkdirs()
                        //make file we're saving to
                        val file = File(dir, model.id + ".png")
                        try {
                            //make sure file exists
                            file.createNewFile()
                            //open stream
                            val fOut = FileOutputStream(file)
                            // Use the compress method on the BitMap object to write image to the OutputStream
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                            fOut.flush()
                            fOut.close()
                            Toast.makeText(VerdantApp.instance, "Saved!", Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                })
    }

    /**
     * Sets proper FAB toggle state during opening.
     *
     * @param image The image we're checking the state of.
     */
    private fun checkFavoriteImage(image: ImgurImage) {
        val jr = object : JsonObjectRequest(
                Request.Method.GET,
                "https://api.imgur.com/3/image/" + image.id, null,
                Response.Listener { jo ->
                    try {
                        val fav = jo.getJSONObject("data").getBoolean("favorite")
                        detailView.checkFAB(fav)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener({ it.printStackTrace() })) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] =
                        "Bearer ${VerdantApp.instance
                                .getSharedPreferences(ImgurAPI.PREFS_NAME, 0)
                                .getString(ACCESSTOKEN, "")}"
                return params
            }
        }
        VerdantApp.volleyRequestQueue.add<JSONObject>(jr)
    }
}
