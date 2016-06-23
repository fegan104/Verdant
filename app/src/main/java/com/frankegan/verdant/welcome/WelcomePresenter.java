package com.frankegan.verdant.welcome;

import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.models.ImgurUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by frankegan on 5/17/16.
 */
public class WelcomePresenter implements WelcomeContract.UserActionsListener {

    String response;
    WelcomeContract.View view;

    private static final Pattern accessTokenPattern = Pattern.compile("access_token=([^&]*)");
    private static final Pattern refreshTokenPattern = Pattern.compile("refresh_token=([^&]*)");
    private static final Pattern expiresInPattern = Pattern.compile("expires_in=(\\d+)");
    private static final Pattern accountUsernamePattern = Pattern.compile("account_username=([^&]*)");

    public WelcomePresenter(String response, WelcomeContract.View view) {
        this.response = response;
        this.view = view;
    }

    @Override
    public void explore() {
        view.close();
    }

    @Override
    public void saveUser() {
        // intercept the tokens
        // http://example.com#access_token=ACCESS_TOKEN&token_type=Bearer&expires_in=TIME
        if (response.startsWith(ImgurAPI.IMGUR_REDIRECT_URL)) {
            Matcher m;

            m = refreshTokenPattern.matcher(response);
            m.find();
            String refreshToken = m.group(1);

            m = accessTokenPattern.matcher(response);
            m.find();
            String accessToken = m.group(1);

            m = expiresInPattern.matcher(response);
            m.find();
            long expiresIn = Long.valueOf(m.group(1));

            m = accountUsernamePattern.matcher(response);
            m.find();
            String accountUsername = m.group(1);

            ImgurAPI.saveResponse(new ImgurUser(accessToken, refreshToken, expiresIn, accountUsername));

            view.setWelcomeName(ImgurAPI.getAccountName());
        }
    }
}
