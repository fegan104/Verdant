package com.frankegan.verdant.loginsuccess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.frankegan.verdant.R;

public class LoginSuccessActivity extends AppCompatActivity implements LoginSuccessContract.View{

    private TextView welcomeText;
    private Button exploreButton;
    private LoginSuccessContract.UserActionsListener actionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_success_activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        welcomeText = (TextView) findViewById(R.id.welcome_text);

        Intent intent = getIntent();
        String responseURL = intent.getDataString();
        actionListener = new LoginSuccessPresenter(responseURL, this);
        actionListener.saveUser();
        //
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
