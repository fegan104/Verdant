package com.frankegan.verdant.home

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.frankegan.verdant.R
import com.frankegan.verdant.models.ImgurImage
import com.frankegan.verdant.utils.AnimUtils
import com.frankegan.verdant.utils.OptimisticRequestListener
import com.frankegan.verdant.utils.lollipop


/**
 * This interface should be implemented by the hosting
 * activity to provide element click callbacks.
 *
 */
typealias ImageItemListener = (clickedImage: ImgurImage, clickedView: View) -> Unit

/**
 * @author frankegan created on 6/2/15.
 */
class ImgurAdapter(private var host: Activity, var itemListener: ImageItemListener) :
        RecyclerView.Adapter<ImgurAdapter.ImgurViewHolder>() {
    /**
     * Our data set in-memory for all the list elements.
     */
    private var myDataset: MutableList<ImgurImage> = ArrayList()

    /**
     * Consider a view model for each [RecyclerView] element.
     */
    inner class ImgurViewHolder(var rootView: View) : RecyclerView.ViewHolder(rootView), View.OnClickListener {
        internal var titleView: TextView = rootView.findViewById(R.id.title_text)
        internal var imageView: ImageView = rootView.findViewById(R.id.tileImage)

        init {
            rootView.setOnClickListener(this)
        }

        internal fun setText(titleString: String) {
            titleView.text = titleString
        }

        internal fun setColor(color: Int) {
            titleView.setBackgroundColor(color)
        }

        fun getRootView(): FrameLayout {
            return rootView as FrameLayout
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            val image = myDataset[position]
            itemListener(image, v)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgurViewHolder {
        // create a new view
        return ImgurViewHolder(
                LayoutInflater.from(host).inflate(R.layout.tile_layout, parent, false))
    }

    @TargetApi(21)
    override fun onBindViewHolder(holder: ImgurViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setText(myDataset[position].title)
        lollipop {
            holder.getRootView().foreground = ContextCompat
                    .getDrawable(host, R.drawable.white_ripple)
        }

        Glide.with(host)
                .asBitmap()
                .load(myDataset[position].medThumbLink)
                .listener(OptimisticRequestListener<Bitmap> { resource ->
                    Palette.from(resource)
                            .clearFilters()
                            .generate { p ->
                                val vibrantSwatch = p.vibrantSwatch ?: return@generate

                                lollipop {
                                    holder.imageView.transitionName = "cover${holder.itemId}"
                                }

                                AnimUtils.animateViewColor(
                                        view = holder.titleView,
                                        startColor = Color.parseColor("white"),
                                        endColor = vibrantSwatch.rgb)

                            }
                })
                .apply(RequestOptions()
                        .placeholder(R.color.material_grey700)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(holder.imageView)
    }

    override fun getItemCount() = myDataset.size

    /**
     * This method updates our dataset from an external source. Called when new data is loaded
     * from a db, api, whatever.
     *
     * @param images The [ImgurImage]s that will now be added to our data set.
     */
    fun updateDataset(images: List<ImgurImage>) {
        myDataset.addAll(images)
        notifyDataSetChanged()
    }

    /**
     * Clears all the data in our data set.
     */
    fun clearData() {
        myDataset = ArrayList()
    }
}
