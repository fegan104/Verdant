package com.frankegan.verdant.utils

import android.animation.*
import android.annotation.TargetApi
import android.view.View
import android.view.animation.PathInterpolator
import androidx.animation.doOnEnd
import com.frankegan.verdant.R

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

    fun animateScaleDown(view: View): Animator {
        val scale = AnimatorInflater.loadAnimator(view.context,
                R.animator.fab_scale_down) as AnimatorSet
        scale.setTarget(view)
        scale.start()
        return scale
    }

    fun animateScaleUp(view: View): Animator {
        val scale = AnimatorInflater.loadAnimator(view.context,
                R.animator.fab_scale_up) as AnimatorSet
        scale.setTarget(view)
        scale.start()
        return scale
    }
}