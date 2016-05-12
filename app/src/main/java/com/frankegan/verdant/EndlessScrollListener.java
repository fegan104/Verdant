package com.frankegan.verdant;

/**
 * @author frankegan created on 6/5/15.
 */

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    /**
     * The total number of items in the dataset after the last load
     */
    private int previousTotal = 0;
    /**
     * True if we are still waiting for the last set of data to load.
     */
    private boolean loading = true;
    /**
     * The minimum amount of items to have below your current scroll position before loading more.
     */
    private int visibleThreshold = 5;
    /**
     * Used to keep track of while scrolling.
     */
    int firstVisibleItem, visibleItemCount, totalItemCount;
    /**
     * Always initialized at 0.
     */
    private int current_page = 0;
    /**
     * Needed for getting visible items to signal need for loading.
     */
    private LinearLayoutManager mLinearLayoutManager;

    /**
     * @param linearLayoutManager The LayoutManager for our {@link RecyclerView}.
     */
    public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    /**
     * Called to signal it's time to load more elements from the db, api, or whatever.
     *
     * @param current_page
     */
    public abstract void onLoadMore(int current_page);

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        //for endless scrolling
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached: Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }
    }

}