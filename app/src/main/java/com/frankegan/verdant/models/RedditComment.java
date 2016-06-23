package com.frankegan.verdant.models;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by frankegan on 6/22/16.
 */
public class RedditComment {
    String link;
    String text;
    List<RedditComment> children;

    public RedditComment(String link, String text, @Nullable List<RedditComment> children) {
        this.link = link;
        this.text = text;
        this.children = children;
    }

    public String getLink() {
        return link;
    }

    public String getText() {
        return text;
    }

    public List<RedditComment> getChildren() {
        return children;
    }
}
