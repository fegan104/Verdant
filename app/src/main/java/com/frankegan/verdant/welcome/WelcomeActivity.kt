package com.frankegan.verdant.welcome

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.core.content.edit
import com.frankegan.verdant.R
import com.frankegan.verdant.data.ImgurRepository
import com.frankegan.verdant.data.ImgurUser
import com.frankegan.verdant.data.Result
import com.frankegan.verdant.utils.launchSilent
import kotlinx.android.synthetic.main.welcome_activity.*
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * This [AppCompatActivity] is shown when the user has successfully logged in.
 * It is not the first screen the user sees.
 */
class WelcomeActivity : AppCompatActivity() {

    private val IMGUR_REDIRECT_URL = "verdant://logincallback"
    private val ACCESSTOKEN = "access_token"
    private val token by lazy { defaultSharedPreferences.getString("access_token", "") }
    private val imgurRepository by lazy { ImgurRepository.getInstance(token) }

    private val accessTokenPattern = Pattern.compile("access_token=([^&]*)")
    private val refreshTokenPattern = Pattern.compile("refresh_token=([^&]*)")
    private val expiresInPattern = Pattern.compile("expires_in=(\\d+)")
    private val accountUsernamePattern = Pattern.compile("account_username=([^&]*)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)

        val responseURL = intent.dataString
        saveUser(responseURL)
        //set up explore flat button
        exploreButton.setOnClickListener { finish() }
        launchSilent(UI) {
            val name = imgurRepository.getUsername()
            when (name) {
                is Result.Success -> {
                    welcomeText.text = getString(R.string.welcome_back, name.data)
                }
                is Result.Error -> toast("There was an error saving user")
            }
        }
    }

    fun saveUser(responseUrl: String) = launchSilent(UI) {
        // intercept the tokens
        // http://example.com#access_token=ACCESS_TOKEN&token_type=Bearer&expires_in=TIME
        if (responseUrl.startsWith(IMGUR_REDIRECT_URL)) {
            var m: Matcher

            m = refreshTokenPattern.matcher(responseUrl)
            m.find()
            val refreshToken = m.group(1)

            m = accessTokenPattern.matcher(responseUrl)
            m.find()
            val accessToken = m.group(1)

            m = expiresInPattern.matcher(responseUrl)
            m.find()
            val expiresIn = java.lang.Long.valueOf(m.group(1))!!

            m = accountUsernamePattern.matcher(responseUrl)
            m.find()
            val accountUsername = m.group(1)

            defaultSharedPreferences.edit {
                putString(ACCESSTOKEN, accessToken)
            }
            imgurRepository.saveUser(ImgurUser(
                    username = accountUsername,
                    refreshToken = refreshToken,
                    expiresAt = expiresIn
            ))
        }
    }

}
