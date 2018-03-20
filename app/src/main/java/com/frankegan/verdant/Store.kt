package com.frankegan.verdant

import com.frankegan.verdant.models.Action
import com.frankegan.verdant.models.ImgurImage

/**
 * Created by frankegan on 3/19/18.
 */
interface Store<T> {

    /**
     * This method applies the given action to the store.
     *
     * @param action The action that we will use to create a new store.
     */
    fun dispatch(action: Action)

    /**
     * A Render can subscribe to our store to receive LiveData optionally
     * transformed by the provided Function.
     *
     * @param renderer The renderer that will receive updates form the store.
     * @param func An optional mapping of state to render props.
     */
    fun subscribe(renderer: Renderer<T>, func: (ImgurImage) -> ImgurImage = { it })

    fun reduce(state: T?, action: Action): T
}