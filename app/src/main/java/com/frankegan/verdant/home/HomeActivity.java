package com.frankegan.verdant.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.frankegan.verdant.EndlessScrollListener;
import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.R;
import com.frankegan.verdant.customtabs.CustomTabActivityHelper;
import com.frankegan.verdant.imagedetail.ImageDetailActivity;
import com.frankegan.verdant.models.ImgurImage;
import com.frankegan.verdant.settings.SettingsActivity;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements
        HomeContract.View, SwipeRefreshLayout.OnRefreshListener {

    /**
     * The {@link Toolbar} at the top of our window that will be used to login.
     */
    private Toolbar toolbar;
    /**
     * Adds swipe to refresh feature.
     */
    private SwipeRefreshLayout refreshLayout;
    /**
     * recycler view for all our images.
     */
    private RecyclerView mRecyclerView;
    /**
     * the adapter between data and our {@link RecyclerView}.
     */
    private ImgurAdapter mAdapter;
    /**
     * A reference to the presenter that will handle our user interactions.
     */
    private HomeContract.UserActionsListener actionsListener =
            new HomePresenter(ImgurAPI.getDefaultSubreddit(), this);
    /**
     * Used to let the user change subreddit galleries.
     */
    private FloatingActionButton fab;
    /**
     * Used to warm up login and open login tab.
     */
    private CustomTabActivityHelper customTabActivityHelper = new CustomTabActivityHelper();
    /**
     * This is used to control the bottom sheet used to explore new subreddits.
     */
    private BottomSheetBehavior mBottomSheetBehavior;
    /**
     * This is used to search for and enter new subreddit names in the bottom sheet.
     */
    private EditText newSubEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This activity starts with a launch theme, then we set it to a normal theme here
        setTheme(R.style.Verdant);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Warm up custom tab for login
        customTabActivityHelper.mayLaunchUrl(Uri.parse(ImgurAPI.LOGIN_URL), null, null);

        //Set up recyclerView
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int span = (int) (dpWidth / 180);//grid span
        if (span < 1) span = 1;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(this, span);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Set up progressView and refreshLayout
        int spinnerOffset = getResources().getDimensionPixelSize(R.dimen.spinner_offset);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setProgressViewOffset(true, 0, spinnerOffset);
        refreshLayout.setOnRefreshListener(this);

        //init FAB
        fab = (FloatingActionButton) findViewById(R.id.reddit_fab);
        fab.setOnClickListener(v -> showSubredditChooser());

        //init bottomsheet
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        newSubEdit = (EditText) bottomSheet.findViewById(R.id.new_sub_edit);
        ListView recentsListView = (ListView) bottomSheet.findViewById(R.id.recents_listview);
        //save list
//        String[] listItems = {"banana", "oraiste", "pheitsoga", "android", "item 3", "foobar", "bar"};
//        Set<String> tasksSet = new HashSet<>(Arrays.asList(listItems));
//        Prefs.putStringSet("recent_subreddits", tasksSet);
        //recover list
//        List<String> tasksList = new ArrayList<>(Prefs.getStringSet("recent_subreddits", new HashSet<>()));
//        tasksList.add(0, "New List Item");
        List<String> recents = new ArrayList<>(Prefs.getStringSet("recent_subreddits", new HashSet<>()));
        recentsListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recents));

        //Keep adapter consistent during rotations
        if (mAdapter == null)
            mAdapter = new ImgurAdapter(this, (i, v) -> actionsListener.openImageDetails(i, v));
        if (savedInstanceState == null)
            actionsListener.loadMoreImages(0);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                actionsListener.loadMoreImages(current_page);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
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
            menu.add(0, R.id.logout, 1, "Log out");
        } else if (!ImgurAPI.getInstance().isLoggedIn()) {
            menu.add(0, R.id.tab_login, 1, "Login");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //which settings option was selected
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.tab_login) {
            //launches the Chrome Custom Tab
            ImgurAPI.login(this, customTabActivityHelper.getSession());
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.logout) {
            ImgurAPI.getInstance().logout();
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        refreshLayout.setRefreshing(active);
    }

    @Override
    public void showImages(List<ImgurImage> images) {
        mAdapter.updateDataset(images);
    }

    @Override
    public void showImageDetailUi(ImgurImage image, View v) {
        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA, image);

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, v.findViewById(R.id.net_img),
                        this.getString(R.string.image_transition_name));

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Override
    public void showSubredditChooser() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        newSubEdit.setOnEditorActionListener((TextView v, int id, KeyEvent e) -> {
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                String newTarget = newSubEdit.getText().toString();
                actionsListener.changeSubreddit(newTarget);
                //recover list
                List<String> recents = new ArrayList<>(Prefs.getStringSet("recent_subreddits", new HashSet<>()));
                recents.add(0, newTarget);
                //save list
                Prefs.putStringSet("recent_subreddits", new HashSet<>(recents));
                return true;
            }
            return false;
        });
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.pick_new_subreddit);
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("OK", (d, w) ->
//                actionsListener.changeSubreddit(input.getText().toString()));
//        builder.setNegativeButton("CANCEL", (d, w) -> d.cancel());
//
//        builder.show();
    }

    @Override
    public void clearImages() {
        mAdapter.clearData();
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        mAdapter.clearData();
        mAdapter.notifyDataSetChanged();
        actionsListener.loadMoreImages(0);
        mRecyclerView.setAdapter(mAdapter);
    }
}
