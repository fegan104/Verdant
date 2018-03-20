package com.frankegan.verdant.utils

import android.animation.*
import android.annotation.TargetApi
import android.view.View
import android.view.ViewAnimationUtils
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

    fun animateSheetReveal(sheet: View, start: View) {
        val endRadius = Math.hypot(sheet.width.toDouble(), sheet.height.toDouble())
        val cx = start.x + (start.width / 2)
        val cy = start.y + start.height + 56

        val anim = ViewAnimationUtils.createCircularReveal(sheet,
                cx.toInt(),
                cy.toInt(),
                0f,
                endRadius.toFloat() * 2)
        anim.duration = 700
        anim.start()
    }

    fun animateSheetHide(sheet: View, end: View) {
        val endRadius = Math.hypot(sheet.width.toDouble(), sheet.height.toDouble())
        val cx = end.x + (end.width / 2)
        val cy = end.y + end.height + 56

        val anim = ViewAnimationUtils.createCircularReveal(sheet,
                cx.toInt(),
                cy.toInt(),
                endRadius.toFloat(),
                0f)
        anim.duration = 700
        anim.start()
    }
}