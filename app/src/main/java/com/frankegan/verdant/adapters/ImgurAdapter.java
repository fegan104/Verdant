package com.frankegan.verdant.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    static List<ImgurImage> myDataset = new ArrayList<>();
    Activity host;
    public ImageItemListener itemListener;


    public ImgurAdapter(Activity hostActivity, ImageItemListener itemListener) {
        host = hostActivity;
        this.itemListener = itemListener;
    }

    public class ImgurViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View rootView;
        public TextView title;
        public ImageView imageView;
        public boolean animated = false;

        public ImgurViewHolder(View root) {
            super(root);
            title = (TextView) root.findViewById(R.id.title_text);
            imageView = (ImageView) root.findViewById(R.id.net_img);
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

        View getRootView() {
            return rootView;
        }

        TextView getTitleView() {
            return title;
        }

        ImageView getImageView() {
            return imageView;
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
        return new ImgurViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tile_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ImgurViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setText(myDataset.get(position).getTitle());
// TODO: 5/11/16 delete
//        holder.getRootView().setOnClickListener((View v) -> {
//            ImgurImage imgurImage = myDataset.get(position);
//
//            Intent intent = new Intent(host, ImageDetailActivity.class);
//            intent.putExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA, imgurImage);
//
//            ActivityOptionsCompat options = ActivityOptionsCompat
//                    .makeSceneTransitionAnimation(host, v.findViewById(R.id.net_img),
//                            host.getString(R.string.image_transition_name));
//
//            ActivityCompat.startActivity(host, intent, options.toBundle());
//        });

        Glide.with(host)
                .load(myDataset.get(position).getMediumThumbnailLink())
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
                                    .generate((Palette palette) -> {

                                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                                        if (vibrantSwatch != null) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                                holder.getImageView().setTransitionName("cover" + position);
                                            holder.animated = true;
                                            AnimUtils.animateViewColor(holder.getTitleView(), Color.parseColor("white"),
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

    public interface ImageItemListener{
        void onImageClick(ImgurImage clickedImage, View clickedView);
    }
}
