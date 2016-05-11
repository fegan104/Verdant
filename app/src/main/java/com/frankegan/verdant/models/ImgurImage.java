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
    private final String id,
            title,
            description,
            link,
            smallThumbnailLink,
            medThumbLink,
            largeThumbLink;

    private final Boolean favorited;

    /**
     * All the information needed to create an ImgurImage.
     *
     */
    public ImgurImage(String id,
                      String title,
                      String description,
                      Boolean favorited) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = getLink();
        this.smallThumbnailLink = getSmallThumbnailLink();
        this.medThumbLink = getMediumThumbnailLink();
        this.largeThumbLink = getLargeThumbnailLink();
        this.favorited = favorited;
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
        return "http://i.imgur.com/" + id + "t.png";
    }

    public String getMediumThumbnailLink() {
        return "http://i.imgur.com/" + id + "m.png";
    }

    public String getLargeThumbnailLink() {
        return "http://i.imgur.com/" + id + "h.png";
    }

    public String getLink() {
        return "http://i.imgur.com/" + id + ".png";
    }

    public Boolean isFavorited() {
        return favorited;
    }

    /*Parcel stuff */
    public static final Creator<ImgurImage> CREATOR
            = new Creator<ImgurImage>() {
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
        link = in.readString();
        smallThumbnailLink = in.readString();
        medThumbLink = in.readString();
        largeThumbLink = in.readString();
        favorited = in.readByte() != 0x00;
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
        dest.writeString(link);
        dest.writeString(smallThumbnailLink);
        dest.writeString(medThumbLink);
        dest.writeString(largeThumbLink);
        dest.writeByte((byte) (favorited ? 0x01 : 0x00));
    }
}
