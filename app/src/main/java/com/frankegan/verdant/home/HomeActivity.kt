package com.frankegan.verdant.home

import android.annotation.TargetApi
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
import android.view.inputmethod.EditorInfo
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
import com.frankegan.verdant.utils.AnimUtils
import com.frankegan.verdant.utils.hideKeyboard
import com.frankegan.verdant.utils.lollipop
import kotlinx.android.synthetic.main.home_activity.*
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

class HomeActivity : AppCompatActivity(), HomeContract.View, SwipeRefreshLayout.OnRefreshListener {
    /**
     * the adapter between data and our [RecyclerView].
     */
    private lateinit var imgurAdapter: ImgurAdapter
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
        fun spanCount(): Int {
            val displayMetrics = this.resources.displayMetrics
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
            var span = (dpWidth / 180).toInt()//grid span
            if (span < 1) span = 1
            return span
        }
        recyclerview.layoutManager = GridLayoutManager(this, spanCount())
        //Set up progressView and refreshLayout
        refresh.setProgressViewOffset(true, 0, resources.getDimensionPixelSize(R.dimen.spinner_offset))
        refresh.setOnRefreshListener(this)
        fab.setOnClickListener { showBottomSheet(true) }
        //init bottomsheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        recentsListView.setOnItemClickListener { _, v: View, _, _ ->
            actionsListener.changeSubreddit((v as TextView).text.toString())
        }
        //make sure the recents list is populated
        refreshRecents()
        //Keep adapter consistent during rotations
        imgurAdapter = ImgurAdapter(this) { i, v -> showImageDetailUi(i, v) }
        actionsListener.loadMoreImages(0)
        recyclerview.adapter = imgurAdapter
        recyclerview.addOnScrollListener(object : EndlessScrollListener(recyclerview.layoutManager as LinearLayoutManager) {
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

        newSubEdit.setOnEditorActionListener { _, id: Int, _ ->
            if (id == EditorInfo.IME_ACTION_SEARCH) {//if they hit the search button on their keyboard
                //hide soft keyboard
                hideKeyboard()
                //perform main action of switching subreddit
                val newTarget = newSubEdit.text.toString()
                actionsListener.changeSubreddit(newTarget)
                //clear edit text
                newSubEdit.setText("")
                return@setOnEditorActionListener true
            }
            false
        }

        if (show) {
            lollipop { AnimUtils.animateSheetReveal(bottomSheet, fab) }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            lollipop { AnimUtils.animateSheetHide(bottomSheet, fab) }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun refreshRecents() {
        val recents = ArrayList(defaultSharedPreferences.getStringSet("recent_subreddits", HashSet()))
        recentsListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, recents)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //which settings option was selected
        when (item.itemId) {
            R.id.action_settings -> {
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.tab_login -> {
                //launches the Chrome Custom Tab
                ImgurAPI.login(this, customTabActivityHelper.session)
                invalidateOptionsMenu()
                return true
            }
            R.id.logout -> {
                ImgurAPI.logout()
                invalidateOptionsMenu()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setProgressIndicator(active: Boolean) {
        refresh.isRefreshing = active
    }

    override fun showImages(images: List<ImgurImage>) = imgurAdapter.updateDataset(images)

    override fun showImageDetailUi(image: ImgurImage, view: View) {
        showBottomSheet(false)

        val intent = Intent(this, ImageDetailActivity::class.java)
        intent.putExtra(ImageDetailActivity.IMAGE_DETAIL_EXTRA, image)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.tileImage), this.getString(R.string.image_transition_name))

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    override fun clearImages() {
        imgurAdapter.clearData()
        imgurAdapter.notifyDataSetChanged()
        recyclerview.adapter = imgurAdapter
    }

    override fun setToolbarTitle(title: String) {
        toolbar.title = title.toUpperCase()
    }

    override fun onRefresh() {
        imgurAdapter.clearData()
        imgurAdapter.notifyDataSetChanged()
        actionsListener.loadMoreImages(0)
        recyclerview.adapter = imgurAdapter
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
