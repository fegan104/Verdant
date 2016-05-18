package com.frankegan.verdant.models;

/**
 * Created by frankegan on 5/10/16.
 */
public class ImgurUser {
    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;
    private final String accountUsername;

    public ImgurUser(String accessToken, String refreshToken, long expiresIn, String accountUsername) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.accountUsername = accountUsername;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getAccountUsername() {
        return accountUsername;
    }
}
