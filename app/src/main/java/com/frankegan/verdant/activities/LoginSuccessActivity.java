package com.frankegan.verdant.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.frankegan.verdant.R;

public class LoginSuccessActivity extends AppCompatActivity {

    /**
     * Logging tag.
     */
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_success_activity);

        Intent intent = getIntent();
        Uri data = intent.getData();
        Log.d(TAG, "onCreate: data = " + data);
    }
}
