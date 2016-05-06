package com.frankegan.verdant.utils;

/**
 * Created by frankegan on 1/8/16.
 */

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.animation.PathInterpolator;

/**
 * Utility methods for working with animations.
 */
@TargetApi(21)
public class AnimUtils {

    private static int COLOR_ANIMATION_DURATION = 300;
    private AnimUtils() { }

    /**
     * Change the color of a view with an animation
     *
     * @param v          the view to change the color
     * @param startColor the color to start animation
     * @param endColor   the color to end the animation
     */
    public static void animateViewColor(View v, int startColor, int endColor) {

        ObjectAnimator animator = ObjectAnimator.ofObject(v, "backgroundColor",
                new ArgbEvaluator(), startColor, endColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            animator.setInterpolator(new PathInterpolator(0.4f, 0f, 1f, 1f));
        animator.setDuration(COLOR_ANIMATION_DURATION);
        animator.start();
    }

}