package com.frankegan.verdant.customtabs;

/**
 * @author frankegan created on 10/25/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.frankegan.verdant.activities.AutoLoginActivity;

/**
 * A Fallback that opens a Webview when Custom Tabs is not available
 */
public class WebviewFallback implements CustomTabActivityHelper.CustomTabFallback {
    @Override
    public void openUri(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, AutoLoginActivity.class);
        activity.startActivity(intent);
    }
}