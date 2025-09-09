package com.frankegan.verdant.feature.welcome

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.frankegan.verdant.data.ImgurRepository
import com.frankegan.verdant.data.ImgurUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val IMGUR_REDIRECT_URL = "verdant://logincallback"
private const val ACCESSTOKEN = "access_token"

val Context.defaultSharedPreferences: SharedPreferences
    get() = getSharedPreferences("default", Context.MODE_PRIVATE)

class WelcomeViewModel(private val app: Application): AndroidViewModel(app) {

    private val token by lazy { app.defaultSharedPreferences.getString("access_token", "") ?: "" }
    private val imgurRepository by lazy { ImgurRepository.getInstance(token) }
    private val userFlow = MutableStateFlow<Result<String>?>(null)

    private val accessTokenPattern = Pattern.compile("access_token=([^&]*)")
    private val refreshTokenPattern = Pattern.compile("refresh_token=([^&]*)")
    private val expiresInPattern = Pattern.compile("expires_in=(\\d+)")
    private val accountUsernamePattern = Pattern.compile("account_username=([^&]*)")

    fun saveUser(responseUrl: String) = viewModelScope.launch {
        // intercept the tokens
        // http://example.com#access_token=ACCESS_TOKEN&token_type=Bearer&expires_in=TIME
        if (responseUrl.startsWith(IMGUR_REDIRECT_URL)) {
            var m: Matcher = refreshTokenPattern.matcher(responseUrl)
            m.find()
            val refreshToken = m.group(1)

            m = accessTokenPattern.matcher(responseUrl)
            m.find()
            val accessToken = m.group(1)

            m = expiresInPattern.matcher(responseUrl)
            m.find()
            val expiresIn = java.lang.Long.valueOf(m.group(1))

            m = accountUsernamePattern.matcher(responseUrl)
            m.find()
            val accountUsername = m.group(1)

            app.defaultSharedPreferences.edit {
                putString(ACCESSTOKEN, accessToken)
            }
            imgurRepository.saveUser(
                ImgurUser(
                    username = accountUsername,
                    refreshToken = refreshToken,
                    expiresAt = expiresIn
                )
            )

            userFlow.value = imgurRepository.getUsername()
        }
    }

    fun observeAuthState(): Flow<Result<String>> = userFlow.filterNotNull()
}