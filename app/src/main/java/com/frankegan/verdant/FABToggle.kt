package com.frankegan.verdant

import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.ImageButton

/**
 * Created by frankegan on 5/7/16.
 *
 * A [Checkable] [ImageButton] which has a minimum offset i.e. translation Y.
 */
class FABToggle(context: Context, attrs: AttributeSet) : AppCompatImageButton(context, attrs), Checkable {

    private var isChecked = false

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun setChecked(isChecked: Boolean) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked
            refreshDrawableState()
        }
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }

}