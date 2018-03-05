package com.frankegan.verdant.welcome

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.welcome_activity.*
import android.widget.Button
import android.widget.TextView

import com.frankegan.verdant.R

/**
 * This [AppCompatActivity] is shown when the user has successfully logged in.
 * It is not the first screen the user sees.
 */
class WelcomeActivity : AppCompatActivity(), WelcomeContract.View {

    private var actionListener: WelcomeContract.UserActionsListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        //pass off model
        val intent = intent
        val responseURL = intent.dataString
        actionListener = WelcomePresenter(responseURL, this)
        actionListener!!.saveUser()
        //set up explore flat button
        exploreButton.setOnClickListener { v -> actionListener!!.explore() }
    }

    override fun onBackPressed() {
        close()
    }

    override fun setWelcomeName(accountName: String) {
        val welcome = getString(R.string.welcome_back) + accountName + ","
        welcomeText!!.text = welcome
    }

    override fun close() {
        finish()
    }
}
