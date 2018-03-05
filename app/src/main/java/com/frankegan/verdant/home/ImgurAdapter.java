package com.frankegan.verdant.home;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.frankegan.verdant.R;
import com.frankegan.verdant.models.ImgurImage;
import com.frankegan.verdant.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author frankegan created on 6/2/15.
 */
public class ImgurAdapter extends RecyclerView.Adapter<ImgurAdapter.ImgurViewHolder> {
    /**
     * Our data set in-memory for all the list elements.
     */
    static List<ImgurImage> myDataset = new ArrayList<>();
    /**
     * The hosting activity, this is needed for {@link android.content.Context}.
     */
    Activity host;
    /**
     * This interface is used to communicate to the hosting
     * {@link com.frankegan.verdant.home.HomeContract.View} that we've clicked an element.
     */
    public ImageItemListener itemListener;

    /**
     *
     * @param hostActivity The hosting activity of the{@link RecyclerView}.
     * @param itemListener Receives a call when a list element is clicked, probably hosting activity.
     */
    public ImgurAdapter(Activity hostActivity, ImageItemListener itemListener) {
        host = hostActivity;
        this.itemListener = itemListener;
    }

    /**
     * Consider a view model for each {@link RecyclerView} element.
     */
    public class ImgurViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View rootView;
        public TextView title;
        public ImageView imageView;
        public boolean animated = false;//calculate/animate image color?

        public ImgurViewHolder(View root) {
            super(root);
            title = (TextView) root.findViewById(R.id.title_text);
            imageView = (ImageView) root.findViewById(R.id.tileImage);
            rootView = root;
            rootView.setOnClickListener(this);
        }

        void setText(String titleString) {
            if (title != null)
                title.setText(titleString);
        }

        void setColor(int color) {
            title.setBackgroundColor(color);
        }

        TextView getTitleView() {
            return title;
        }

        ImageView getImageView() {
            return imageView;
        }

        public FrameLayout getRootView() {
            return (FrameLayout) rootView;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ImgurImage image = myDataset.get(position);
            itemListener.onImageClick(image, v);
        }
    }

    @Override
    public ImgurViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        return new ImgurViewHolder(
                LayoutInflater.from(host).inflate(R.layout.tile_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ImgurViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setText(myDataset.get(position).getTitle());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            holder.getRootView().setForeground(ContextCompat.getDrawable(host, R.drawable.white_ripple));

        Glide.with(host)
                .load(myDataset.get(position).getMedThumbLink())
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e,
                                               String model,
                                               Target<Bitmap> target,
                                               boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource,
                                                   String model,
                                                   Target<Bitmap> target,
                                                   boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        if (resource != null && !holder.animated) {
                            Palette.from(resource)
                                    .clearFilters()
                                    .generate(p -> {
                                        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();

                                        if (vibrantSwatch != null) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                                holder.getImageView().setTransitionName("cover" + holder.getAdapterPosition());
                                            holder.animated = true;
                                            AnimUtils.INSTANCE.animateViewColor(holder.getTitleView(), Color.parseColor("white"),
                                                    vibrantSwatch.getRgb());
                                        }
                                    });
                        }
                        return false;
                    }
                })
                .placeholder(R.color.material_grey700)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return myDataset.size();
    }

    /**
     * This method updates our dataset from an external source. Called when new data is loaded
     * from a db, api, whatever.
     *
     * @param images The {@link ImgurImage}s that will now be added to our data set.
     */
    public void updateDataset(List<ImgurImage> images) {
        myDataset.addAll(images);
        notifyDataSetChanged();
    }

    /**
     * Clears all the data in our data set.
     */
    public void clearData() {
        int size = myDataset.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                myDataset.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    /**
     * This interface should be implemented by the hosting
     * activity to provide element click callbacks.
     *
     */
    public interface ImageItemListener{
        /**
         *
         * @param clickedImage The model for the image clicked.
         * @param clickedView The {@link View} that was clicked, this is needed for
         *                    shared element transitions.
         */
        void onImageClick(ImgurImage clickedImage, View clickedView);
    }
}
