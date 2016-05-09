package com.frankegan.verdant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginSuccessActivity extends AppCompatActivity {

    /**
     * Logging tag.
     */
    private String TAG = getClass().getSimpleName();
    private Toolbar toolbar;
    private TextView welcomeText;
    private Button exploreButton;

    private static final Pattern accessTokenPattern = Pattern.compile("access_token=([^&]*)");
    private static final Pattern refreshTokenPattern = Pattern.compile("refresh_token=([^&]*)");
    private static final Pattern expiresInPattern = Pattern.compile("expires_in=(\\d+)");
    private static final Pattern tokenTypePattern = Pattern.compile("token_type=([^&]*)");
    private static final Pattern accountUsernamePattern = Pattern.compile("account_username=([^&]*)");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_success_activity);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        welcomeText = (TextView) findViewById(R.id.welcome_text);

        Intent intent = getIntent();
        String responseURL = intent.getDataString();
        parseIntent(responseURL);

        String welcome = "Welcome back "
                + getSharedPreferences(ImgurAPI.PREFS_NAME, MODE_PRIVATE)
                .getString("account_username", null);
        welcomeText.setText(welcome);

        Toast.makeText(LoginSuccessActivity.this, "access_token = "
                + getSharedPreferences(ImgurAPI.PREFS_NAME, MODE_PRIVATE)
                .getString("access_token", null), Toast.LENGTH_SHORT).show();

        exploreButton = (Button)findViewById(R.id.explore_btn);
        exploreButton.setOnClickListener((View v) -> finish());
    }

    /**
     * Parses and saves the data from the callback response.
     * @param url The data from a deep link back to the activity.
     */
    void parseIntent(String url){
        // intercept the tokens
        // http://example.com#access_token=ACCESS_TOKEN&token_type=Bearer&expires_in=3600
        if (url.startsWith(ImgurAPI.IMGUR_REDIRECT_URL)) {
            Matcher m;

            m = refreshTokenPattern.matcher(url);
            m.find();
            String refreshToken = m.group(1);

            m = accessTokenPattern.matcher(url);
            m.find();
            String accessToken = m.group(1);

            m = expiresInPattern.matcher(url);
            m.find();
            long expiresIn = Long.valueOf(m.group(1));

            m = tokenTypePattern.matcher(url);
            m.find();
            String tokenType = m.group(1);

            m = accountUsernamePattern.matcher(url);
            m.find();
            String accountUsername = m.group(1);

            ImgurAPI.saveResponse(accessToken, refreshToken, expiresIn, tokenType, accountUsername);

        }
    }
}
