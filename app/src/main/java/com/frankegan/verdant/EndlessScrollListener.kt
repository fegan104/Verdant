package com.frankegan.verdant

/**
 * @author frankegan created on 6/5/15.
 */

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class EndlessScrollListener(private val layout: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    /**
     * The total number of items in the dataset after the last load
     */
    private var previousTotal = 0
    /**
     * True if we are still waiting for the last set of data to load.
     */
    private var loading = true
    /**
     * The minimum amount of items to have below your current scroll position before loading more.
     */
    private val visibleThreshold = 5
    /**
     * Used to keep track of while scrolling.
     */
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0
    /**
     * Always initialized at 0.
     */
    private var current_page = 0

    /**
     * Called to signal it's time to load more elements from the db, api, or whatever.
     *
     * @param current_page
     */
    abstract fun onLoadMore(current_page: Int)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        totalItemCount = layout.itemCount
        firstVisibleItem = layout.findFirstVisibleItemPosition()

        //for endless scrolling
        if (loading && (totalItemCount > previousTotal)) {
            loading = false
            previousTotal = totalItemCount
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            // End has been reached: Do something
            current_page++

            onLoadMore(current_page)

            loading = true
        }
    }

}