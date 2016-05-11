package com.frankegan.verdant.models;

/**
 * Created by frankegan on 5/10/16.
 */
public class ImgurUser {
    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;
    private final String tokenType;
    private final String accountUsername;

    public ImgurUser(String accessToken, String refreshToken, long expiresIn, String tokenType, String accountUsername) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.accountUsername = accountUsername;
    }
}
