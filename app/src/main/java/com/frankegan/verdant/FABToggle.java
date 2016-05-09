package com.frankegan.verdant;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageButton;

/**
 * Created by frankegan on 5/7/16.
 *
 * A {@link Checkable} {@link ImageButton} which has a minimum offset i.e. translation Y.
 */
public class FABToggle extends ImageButton implements Checkable {

    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    private boolean isChecked = false;
    private int minOffset;

    public FABToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOffset(int offset) {
        offset = Math.max(minOffset, offset);
        if (getTranslationY() != offset) {
            setTranslationY(offset);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setMinOffset(int minOffset) {
        this.minOffset = minOffset;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean isChecked) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked;
            refreshDrawableState();
        }
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

}