package com.frankegan.verdant.imagedetail


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.frankegan.verdant.Renderer
import com.frankegan.verdant.Store
import com.frankegan.verdant.VerdantApp
import com.frankegan.verdant.api.ImgurApiService
import com.frankegan.verdant.fullscreenimage.FullscreenImageActivity
import com.frankegan.verdant.models.Action
import com.frankegan.verdant.models.ImgurImage
import com.frankegan.verdant.models.ToggleFavorite
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by frankegan on 5/14/16.
 */
class ImageDetailPresenter : ViewModel(), Store<ImgurImage> {
    val state: MutableLiveData<ImgurImage> = MutableLiveData()
    //Default state that will be rendered initially
    private val initState = ImgurImage(
            id = "",
            title = "",
            views = 0,
            description = null)

    override fun subscribe(renderer: Renderer<ImgurImage>, func: (ImgurImage) -> ImgurImage) {
        renderer.render(Transformations.map(state, func))
    }

    override fun dispatch(action: Action) {
        println("oldState = ${state.value}")
        println("action = $action")
        state.value = reduce(state.value, action)
        println("newState = ${state.value}")
        println("⬇️")
    }

    override fun reduce(state: ImgurImage?, action: Action): ImgurImage {
        val newState = state ?: initState

        return when (action) {
            is ToggleFavorite -> newState.copy(favorite = !newState.favorite)
        }
    }

    fun toggleFavoriteImage(id: String) = async(UI) {
        val res = ImgurApiService.create().toggleFavoriteImage(id).await()

        return@async if (res.success) (res.data == "favorite") else false
    }

    //TODO(convert this to not be here)
    fun downloadImage(model: ImgurImage) {
        //download image
        Glide.with(VerdantApp.instance)
                .asBitmap()
                .load(model.link)
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
}
