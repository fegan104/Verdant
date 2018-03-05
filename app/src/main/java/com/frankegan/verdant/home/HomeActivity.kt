package com.frankegan.verdant.home

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.frankegan.verdant.EndlessScrollListener
import com.frankegan.verdant.ImgurAPI
import com.frankegan.verdant.R
import com.frankegan.verdant.customtabs.CustomTabActivityHelper
import com.frankegan.verdant.imagedetail.ImageDetailActivity
import com.frankegan.verdant.models.ImgurImage
import com.frankegan.verdant.settings.SettingsActivity
import com.frankegan.verdant.utils.lollipop
import com.frankegan.verdant.utils.prelollipop
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.tile_layout.*
import java.util.*

class HomeActivity : AppCompatActivity(), HomeContract.View, SwipeRefreshLayout.OnRefreshListener {
    /**
     * the adapter between data and our [RecyclerView].
     */
    private lateinit var mAdapter: ImgurAdapter
    /**
     * A reference to the presenter that will handle our user interactions.
     */
    private lateinit var actionsListener: HomeContract.UserActionsListener
    /**
     * Used to warm up login and open login tab.
     */
    private val customTabActivityHelper = CustomTabActivityHelper()
    /**
     * This is used to control the bottom sheet used to explore new subreddits.
     */
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        //This activity starts with a launch theme, then we set it to a normal theme here
        setTheme(R.style.Verdant)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        setSupportActionBar(toolbar)
        //keep sub reddit when we recreate the activity
        actionsListener = if (savedInstanceState?.getString("SUBREDDIT") != null) {
            HomePresenter(savedInstanceState.getString("SUBREDDIT"), this)
        } else {
            HomePresenter(ImgurAPI.defaultSubreddit, this)
        }
        //Warm up custom tab for login
        customTabActivityHelper.mayLaunchUrl(Uri.parse(ImgurAPI.LOGIN_URL), null, null)

        //Set up recyclerView
        val displayMetrics = this.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        var span = (dpWidth / 180).toInt()//grid span
        if (span < 1) span = 1

        val mLayoutManager: RecyclerView.LayoutManager
        mLayoutManager = GridLayoutManager(this, span)
        recyclerview.layoutManager = mLayoutManager

        //Set up progressView and refreshLayout
        val spinnerOffset = resources.getDimensionPixelSize(R.dimen.spinner_offset)

        refresh.setProgressViewOffset(true, 0, spinnerOffset)
        refresh.setOnRefreshListener(this)

        fab.setOnClickListener { showSubredditChooser() }

        //init bottomsheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        recentsListView.setOnItemClickListener { _, v: View, _, _ ->
            actionsListener.changeSubreddit((v as TextView).text.toString())
        }
        //make sure the recents list is populated
        refreshRecents()

        //Keep adapter consistent during rotations
        mAdapter = ImgurAdapter(this) { i, _ -> showImageDetailUi(i) }
        if (savedInstanceState == null) actionsListener.loadMoreImages(0)

        recyclerview.adapter = mAdapter
        recyclerview.addOnScrollListener(object : EndlessScrollListener(mLayoutManager as LinearLayoutManager) {
            override fun onLoadMore(current_page: Int) {
                actionsListener.loadMoreImages(current_page)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        customTabActivityHelper.bindCustomTabsService(this)
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        setToolbarTitle(actionsListener.subreddit)
    }

    override fun onStop() {
        super.onStop()
        customTabActivityHelper.unbindCustomTabsService(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        if (ImgurAPI.isLoggedIn) {
            menu.add(0, R.id.logout, 1, "Log out")
        } else if (!ImgurAPI.isLoggedIn) {
            menu.add(0, R.id.tab_login, 1, "Login")
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun showBottomSheet(show: Boolean) {
        val endRadius = Math.hypot(bottomSheet.width.toDouble(), bottomSheet.height.toDouble()).toInt()

        if (show) {
            lollipop {
                // create the animator for this view (the start radius is zero)
                val anim = ViewAnimationUtils.createCircularReveal(bottomSheet,
                        fab.right,
                        fab.bottom,
                        0f,
                        (endRadius * 2).toFloat())
                anim.duration = 700
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                anim.start()
            }
            prelollipop { bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED) }

        } else {
            lollipop {
                // create the animator for this view (the end radius is zero)
                val anim = ViewAnimationUtils.createCircularReveal(bottomSheet,
                        fab.right,
                        fab.bottom,
                        endRadius.toFloat(),
                        0f)
                anim.duration = 700
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                anim.start()
            }
            prelollipop { bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED) }
        }
    }

    override fun refreshRecents() {
        val recents = ArrayList(Prefs.getStringSet("recent_subreddits", HashSet()))
        recentsListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, recents)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        //which settings option was selected
        if (id == R.id.action_settings) {
            val i = Intent(this, SettingsActivity::class.java)
            startActivity(i)
            return true
        } else if (id == R.id.tab_login) {
            //launches the Chrome Custom Tab
            ImgurAPI.login(this, customTabActivityHelper.session)
            invalidateOptionsMenu()
            return true
        } else if (id == R.id.logout) {
            ImgurAPI.logout()
            invalidateOptionsMenu()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setProgressIndicator(active: Boolean) {
        refresh.isRefreshing = active
    }

    override fun showImages(images: List<ImgurImage>) {
        mAdapter.updateDataset(images)
    }

    override fun showImageDetailUi(image: ImgurImage) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        val intent = Intent(this, ImageDetailActivity::class.java)
        intent.putExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA, image)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, tileImage, this.getString(R.string.image_transition_name))

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    override fun showSubredditChooser() {
        showBottomSheet(true)
        newSubEdit.setOnEditorActionListener { _, id: Int, _ ->
            if (id == EditorInfo.IME_ACTION_SEARCH) {//if they hit the search button on their keyboard
                //hide soft keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(newSubEdit.windowToken, 0)
                //perform main action of switching subreddit
                val newTarget = newSubEdit.text.toString()
                actionsListener.changeSubreddit(newTarget)
                //clear edit text
                newSubEdit.setText("")
                return@setOnEditorActionListener true
            }
            false
        }
    }

    override fun clearImages() {
        mAdapter.clearData()
        mAdapter.notifyDataSetChanged()
        recyclerview.adapter = mAdapter
    }

    override fun setToolbarTitle(title: String) {
        toolbar.title = title.toUpperCase()
    }

    override fun onRefresh() {
        mAdapter.clearData()
        mAdapter.notifyDataSetChanged()
        actionsListener.loadMoreImages(0)
        recyclerview.adapter = mAdapter
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("SUBREDDIT", actionsListener.subreddit)
    }

    override fun onBackPressed() {
        //hide bottom sheet if visible otherwise don't mess with it
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            showBottomSheet(false)
        } else {
            super.onBackPressed()
        }
    }

}
