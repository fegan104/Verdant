package com.frankegan.verdant

import android.arch.lifecycle.LiveData

/**
 * Created by frankegan on 3/19/18.
 */
interface Renderer<T> {
    /**
     * To render a ui you need a model.
     */
    fun render(model: LiveData<T>)
}