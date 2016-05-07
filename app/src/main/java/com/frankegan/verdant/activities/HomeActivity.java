package com.frankegan.verdant.activities;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.android.volley.VolleyError;
import com.frankegan.verdant.EndlessRecyclerOnScrollListener;
import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.OnAppBarChangeListener;
import com.frankegan.verdant.R;
import com.frankegan.verdant.adapters.ImgurAdapter;
import com.frankegan.verdant.customtabs.CustomTabActivityHelper;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements OnAppBarChangeListener {

    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private ImgurAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String SUBREDDIT = "itookapicture";//default is r/itookapicture cuz it's kind of pretty

    CustomTabActivityHelper customTabActivityHelper = new CustomTabActivityHelper();

    String url = "https://api.imgur.com/oauth2/authorize?client_id=" + ImgurAPI.IMGUR_CLIENT_ID + "&response_type=token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //warm up custom tab for login
        customTabActivityHelper.mayLaunchUrl(Uri.parse(url), null, null);

        //set up recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        int span = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 3;//TODO decide based on dp
        mLayoutManager = new GridLayoutManager(this, span);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //set up progressView and refreshLayout
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        int uiHeight = toolbar.getHeight() + getStatusBarHeight();
        int spinnerOffset = getResources().getDimensionPixelSize(R.dimen.spinner_offset);
        refreshLayout.setProgressViewOffset(true, uiHeight, uiHeight + spinnerOffset);
        refreshLayout.setOnRefreshListener(() -> {
            mAdapter.clearData();
            mAdapter.notifyDataSetChanged();
            loadPageForActivity(0);
            mRecyclerView.setAdapter(mAdapter);
        });

        //keep adapter consistent during rotations
        if (mAdapter == null)
            mAdapter = new ImgurAdapter(this);
        if (savedInstanceState == null)
            loadPageForActivity(0);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(HomeActivity.class.getSimpleName(), "Loading More");
                loadPageForActivity(current_page);
            }

            @Override
            public void onShow() {
                HomeActivity.this.onAppBarScrollIn();
            }

            @Override
            public void onHide() {
                HomeActivity.this.onAppBarScrollOut();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImgurAPI.getInstance().refreshAccessToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customTabActivityHelper.setConnectionCallback(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (ImgurAPI.getInstance().isLoggedIn()) {
            Log.i("frankegan", "Logged in");
            menu.add(0, R.id.logout, 1, "Log out");
        } else if (!ImgurAPI.getInstance().isLoggedIn()) {
            Log.i("frankegan", "Logged out");
            menu.add(0, R.id.tab_login, 1, "Login");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //which settings option was selected
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.tab_login) {
            //launches the Chrome Custom Tab
            CustomTabsIntent customTabsIntent
                    = new CustomTabsIntent.Builder(customTabActivityHelper.getSession()).build();
            final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR
                    = "android.support.customtabs.extra.TOOLBAR_COLOR";
            customTabsIntent.intent.putExtra(EXTRA_CUSTOM_TABS_TOOLBAR_COLOR,
                    ContextCompat.getColor(this, R.color.material_lightgreen500));

            CustomTabActivityHelper.openCustomTab(this,
                    customTabsIntent,
                    Uri.parse(url));

            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.logout) {
            Log.i(getClass().getSimpleName(), "Logging out");
            ImgurAPI.getInstance().logout();
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAppBarScrollOut() {
        toolbar.animate().translationY(-(toolbar.getHeight() + getStatusBarHeight()))
                .setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void onAppBarScrollIn() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    /**
     * This method gets called in {@link android.app.Activity::onCreate} to get status bar height so padding isn't messed up.
     * @return the height of the status bar.
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Makes a request to load more data for the next page of pictures.
     * @param newPage  the page number that you want to request.
     */
    void loadPageForActivity(int newPage) {
        ImgurAPI.getInstance().loadPage(
                (JSONObject response) -> {
                    mAdapter.setDatafromJSON(response);
                    refreshLayout.setRefreshing(false);
                },
                (VolleyError e) -> Log.e(getClass().getSimpleName(), e.toString()),
                SUBREDDIT,
                newPage);
    }
}
