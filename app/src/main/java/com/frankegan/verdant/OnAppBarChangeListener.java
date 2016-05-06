package com.frankegan.verdant;

/**
 * @author frankegan created on 6/6/15.
 */
public interface OnAppBarChangeListener {
    int VISIBLE = 1;
    int GONE = 0;
    void onAppBarScrollOut();
    void onAppBarScrollIn();
}
