package com.frankegan.verdant.welcome

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.frankegan.verdant.R
import kotlinx.android.synthetic.main.welcome_activity.*

/**
 * This [AppCompatActivity] is shown when the user has successfully logged in.
 * It is not the first screen the user sees.
 */
class WelcomeActivity : AppCompatActivity(), WelcomeContract.View {

    private lateinit var actionListener: WelcomeContract.UserActionsListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)

        val responseURL = intent.dataString
        actionListener = WelcomePresenter(responseURL, this)
        actionListener.saveUser()
        //set up explore flat button
        exploreButton.setOnClickListener { actionListener.explore() }
    }

    override fun onBackPressed() {
        close()
    }

    override fun setWelcomeName(accountName: String) {
        welcomeText.text = "${getString(R.string.welcome_back)} $accountName,"
    }

    override fun close() {
        finish()
    }
}
