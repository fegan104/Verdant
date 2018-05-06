package com.frankegan.verdant.welcome

import com.frankegan.verdant.utils.ImgurAPI
import com.frankegan.verdant.data.ImgurUser
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by frankegan on 5/17/16.
 */
class WelcomePresenter(internal var response: String, internal var view: WelcomeContract.View) : WelcomeContract.UserActionsListener {


    override fun saveUser() {
        // intercept the tokens
        // http://example.com#access_token=ACCESS_TOKEN&token_type=Bearer&expires_in=TIME
        if (response.startsWith(ImgurAPI.IMGUR_REDIRECT_URL)) {
            var m: Matcher

            m = refreshTokenPattern.matcher(response)
            m.find()
            val refreshToken = m.group(1)

            m = accessTokenPattern.matcher(response)
            m.find()
            val accessToken = m.group(1)

            m = expiresInPattern.matcher(response)
            m.find()
            val expiresIn = java.lang.Long.valueOf(m.group(1))!!

            m = accountUsernamePattern.matcher(response)
            m.find()
            val accountUsername = m.group(1)

            ImgurAPI.saveResponse(ImgurUser(
                    username = accountUsername,
                    accessToken = accessToken,
                    expiresAt = Calendar.getInstance().timeInMillis + expiresIn,
                    refreshToken = refreshToken
            ))
        }
    }

    companion object {

        private val accessTokenPattern = Pattern.compile("access_token=([^&]*)")
        private val refreshTokenPattern = Pattern.compile("refresh_token=([^&]*)")
        private val expiresInPattern = Pattern.compile("expires_in=(\\d+)")
        private val accountUsernamePattern = Pattern.compile("account_username=([^&]*)")
    }
}
