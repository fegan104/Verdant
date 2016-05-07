package com.frankegan.verdant.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.frankegan.verdant.activities.ImageDetailActivity;
import com.frankegan.verdant.models.ImgurImage;
import com.frankegan.verdant.utils.AnimUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * @author frankegan created on 6/2/15.
 */
public class ImgurAdapter extends RecyclerView.Adapter<ImgurAdapter.ImgurViewHolder> {
    static ArrayList<ImgurImage> myDataset = new ArrayList<>();
    int COLOR_ANIMATION_DURATION = 300;
    public Activity host;

    public ImgurAdapter(Activity hostActivity) {
        host = hostActivity;
    }

    public static class ImgurViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView title;
        public ImageView imageView;
        public boolean animated = false;

        public ImgurViewHolder(View root) {
            super(root);
            title = (TextView) root.findViewById(R.id.title_text);
            imageView = (ImageView) root.findViewById(R.id.net_img);
            rootView = root;
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
        holder.setText(myDataset.get(position).title);

        holder.getRootView().setOnClickListener((View v) -> {
            ImgurImage imgurImage = myDataset.get(position);

            Intent intent = new Intent(host, ImageDetailActivity.class);
            intent.putExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA, imgurImage);

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(host, v.findViewById(R.id.net_img),
                            host.getString(R.string.image_transition_name));

            ActivityCompat.startActivity(host, intent, options.toBundle());
        });

        Glide.with(host)
                .load(myDataset.get(position).medThumbLink)
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
     * This method updates our dataset from a JSON response.
     *
     * @param response The JSON response for the next page from Imgur.
     */
    public void setDataFromJSON(JSONObject response) {
        JSONArray responseJSONArray;
        try {
            responseJSONArray = response.getJSONArray("data");
            for (int i = 0; i < responseJSONArray.length(); i++) {
                JSONObject responseObj = responseJSONArray.getJSONObject(i);
                ImgurImage datum = new ImgurImage(
                        responseObj.get("id").toString(),
                        responseObj.get("title").toString(),
                        responseObj.get("description").toString(),
                        responseObj.getBoolean("favorite"));
                myDataset.add(datum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    /**
     * Clears all the data in our dataset.
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
}
