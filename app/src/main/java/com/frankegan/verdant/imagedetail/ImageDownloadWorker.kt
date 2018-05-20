package com.frankegan.verdant.imagedetail

import android.os.Environment
import androidx.work.Worker
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import java.io.File

class ImageDownloadWorker : Worker() {

    override fun doWork(): WorkerResult {
        val link = inputData.getString(KEY_LINK_ARG, "")
        if (link.isEmpty()) return WorkerResult.FAILURE
        return if (downloadImage(link)) {
            WorkerResult.SUCCESS
        } else {
            WorkerResult.RETRY
        }
    }

    /**
     * Attempts to download the requested image and save it to pictures environment under /Verdant.
     *
     * @return Function returns true if teh image is downloaded without exception, false otherwise.
     */
    private fun downloadImage(downloadLink: String): Boolean {
        //save to pictures directory on external storage
        val filePath = "${Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .absolutePath}/Verdant"
        //make directory
        val sdcardDir = File(filePath).apply { mkdirs() }
        //make file we're saving to
        val sdcardImage = File(sdcardDir, "$downloadLink.png")
        return try {
            val cachedImage = Glide.with(applicationContext)
                    .downloadOnly()
                    .load(downloadLink)
                    .submit(SimpleTarget.SIZE_ORIGINAL, SimpleTarget.SIZE_ORIGINAL)
                    .get()
            cachedImage?.copyTo(sdcardImage)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        const val KEY_LINK_ARG = "link_arg"
    }
}