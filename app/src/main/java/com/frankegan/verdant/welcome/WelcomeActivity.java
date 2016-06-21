package com.frankegan.verdant.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.frankegan.verdant.R;

/**
 * This {@link AppCompatActivity} is shown when the user has successfully logged in.
 * It is not the first screen the user sees.
 */
public class WelcomeActivity extends AppCompatActivity implements WelcomeContract.View{

    private TextView welcomeText;
    private Button exploreButton;
    private WelcomeContract.UserActionsListener actionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        welcomeText = (TextView) findViewById(R.id.welcome_text);
        //pass off model
        Intent intent = getIntent();
        String responseURL = intent.getDataString();
        actionListener = new WelcomePresenter(responseURL, this);
        actionListener.saveUser();
        //set up explore flat button
        exploreButton = (Button)findViewById(R.id.explore_btn);
        exploreButton.setOnClickListener(v -> actionListener.explore());
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    public void setWelcomeName(String accountName) {
        String welcome = getString(R.string.welcome_back) + accountName + ",";
        welcomeText.setText(welcome);
    }

    @Override
    public void close() {
        finish();
    }
}
