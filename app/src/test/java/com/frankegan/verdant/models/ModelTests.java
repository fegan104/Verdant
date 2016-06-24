package com.frankegan.verdant.models;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by frankegan on 6/23/16.
 */
public class ModelTests {
    ImgurImage imgurImage = new ImgurImage("id", "title", "description", true, 999);
    ImgurUser imgurUser = new ImgurUser("accessToken", "refreshToken", 999L, "username");

    @Test
    public void imageTest(){
        Assert.assertEquals(imgurImage.getId(), "id");
        Assert.assertEquals(imgurImage.getTitle(), "title");
        Assert.assertEquals(imgurImage.getDescription(), "description");
        Assert.assertTrue(imgurImage.isAnimated());
        Assert.assertEquals(imgurImage.getViews(), 999);
        Assert.assertEquals(imgurImage.getLink(), "https://i.imgur.com/id.jpg");
        Assert.assertEquals(imgurImage.getMediumThumbnailLink(), "https://i.imgur.com/idm.jpg");
        Assert.assertEquals(imgurImage.getLargeThumbnailLink(), "https://i.imgur.com/idh.jpg");
    }

    @Test
    public void userTest(){
        Assert.assertEquals(imgurUser.getAccessToken(), "accessToken");
        Assert.assertEquals(imgurUser.getRefreshToken(), "refreshToken");
        Assert.assertEquals(imgurUser.getExpiresIn(), 999L);
        Assert.assertEquals(imgurUser.getAccountUsername(), "username");
    }
}
