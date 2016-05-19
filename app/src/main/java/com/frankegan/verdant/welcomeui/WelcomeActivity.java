package com.frankegan.verdant.welcomeui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.frankegan.verdant.R;

public class WelcomeActivity extends AppCompatActivity implements WelcomeContract.View{

    private TextView welcomeText;
    private Button exploreButton;
    private WelcomeContract.UserActionsListener actionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        welcomeText = (TextView) findViewById(R.id.welcome_text);

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
        String welcome = "Welcome back " + accountName + ",";
        welcomeText.setText(welcome);
    }

    @Override
    public void close() {
        finish();
    }
}
