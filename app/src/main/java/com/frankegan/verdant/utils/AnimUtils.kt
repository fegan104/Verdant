package com.frankegan.verdant.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.view.View
import android.view.animation.PathInterpolator
import androidx.animation.doOnEnd

/**
 * Taken generously from Nick Butcher's amazing Plaid https://github.com/nickbutcher/plaid
 * Utility methods for working with animations.
 */
@TargetApi(21)
object AnimUtils {
    private const val COLOR_ANIMATION_DURATION = 300L

    /**
     * Change the color of a view with an animation
     *
     * @param v          the view to change the color
     * @param startColor the color to start animation
     * @param endColor   the color to end the animation
     */
    fun animateViewColor(view: View, startColor: Int, endColor: Int) {

        val animator = ObjectAnimator.ofObject(view, "backgroundColor",
                ArgbEvaluator(), startColor, endColor)
        lollipop { animator.interpolator = PathInterpolator(0.4f, 0f, 1f, 1f) }
        view.setHasTransientState(true)
        animator.duration = COLOR_ANIMATION_DURATION
        animator.doOnEnd { view.setHasTransientState(false) }
        animator.start()
    }
}