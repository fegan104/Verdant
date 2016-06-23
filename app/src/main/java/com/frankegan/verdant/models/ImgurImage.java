package com.frankegan.verdant.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author frankegan created on 6/3/15.
 */
public class ImgurImage implements Parcelable {
    /**
     * Attributes of an image from Imgur.
     */
    private final boolean favorited, animated;
    private final int views;
    private final String id, title, description;

    /**
     * All the information needed to create an ImgurImage.
     */
    public ImgurImage(String id,
                      String title,
                      String description,
                      boolean favorited,
                      boolean animated,
                      int views) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.favorited = favorited;
        this.animated = animated;
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSmallThumbnailLink() {
        return "http://i.imgur.com/" + id + "t.jpg";
    }

    public String getMediumThumbnailLink() {
        return "http://i.imgur.com/" + id + "m.jpg";
    }

    public String getLargeThumbnailLink() {
        return "http://i.imgur.com/" + id + "h.jpg";
    }

    public String getLink() {
        return "http://i.imgur.com/" + id + ".jpg";
    }

    public boolean isAnimated() {
        return animated;
    }

    public int getViews() {
        return views;
    }

    /*+++++ Parcel stuff +++++*/
    public static final Creator<ImgurImage> CREATOR = new Creator<ImgurImage>() {
        public ImgurImage createFromParcel(Parcel in) {
            return new ImgurImage(in);
        }

        public ImgurImage[] newArray(int size) {
            return new ImgurImage[size];
        }
    };

    private ImgurImage(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        favorited = in.readByte() != 0x00;
        animated = in.readByte() != 0x00;
        views = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (favorited ? 0x01 : 0x00));
        dest.writeByte((byte) (animated ? 0x01 : 0x00));
        dest.writeInt(views);
    }
}
