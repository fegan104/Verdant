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
    private boolean favorited, animated;
    private int views, points;
    private String id,
            title,
            description,
            account_url;

    /**
     * All the information needed to create an ImgurImage.
     */
    public ImgurImage(String id,
                      String title,
                      String description,
                      String account_url,
                      boolean favorited,
                      boolean animated,
                      int views,
                      int points) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.account_url = account_url;
        this.favorited = favorited;
        this.animated = animated;
        this.views = views;
        this.points = points;
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

    /**
     * @return OP's account name.
     */
    public String getAccountUrl(){
        return account_url;
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

    public int getPoints() {
        return points;
    }

    /*+++++ Parcel stuff +++++*/
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
        account_url = in.readString();
        favorited = in.readByte() != 0x00;
        animated = in.readByte() != 0x00;
        views = in.readInt();
        points = in.readInt();
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
        dest.writeString(account_url);
        dest.writeByte((byte) (favorited ? 0x01 : 0x00));
        dest.writeByte((byte) (animated ? 0x01 : 0x00));
        dest.writeInt(views);
        dest.writeInt(points);
    }
}
