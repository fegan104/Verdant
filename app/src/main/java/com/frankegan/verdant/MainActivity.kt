package com.frankegan.verdant

import android.R.attr.path
import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.frankegan.verdant.feature.home.HomeRoute
import com.frankegan.verdant.feature.home.HomeScreen
import com.frankegan.verdant.feature.imagedetail.ImageDetailRoute
import com.frankegan.verdant.feature.imagedetail.ImageDetailScreen
import com.frankegan.verdant.feature.search.SearchRoute
import com.frankegan.verdant.feature.search.SearchScreen
import com.frankegan.verdant.ui.theme.VerdantTheme


private const val ANIMATION_DURATION_IN_MILLIS = 500

/**
 * Transformation for the shared element bounds.
 * Defines the tween animation for the shared element transitions.
 */
val albumBoundsTransform = { _: Rect, _: Rect ->
    tween<Rect>(durationMillis = ANIMATION_DURATION_IN_MILLIS)
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VerdantTheme {
                val navController = rememberNavController()

                SharedTransitionLayout {
                    NavHost(navController, startDestination = HomeRoute) {
                        composable<HomeRoute> {
                            HomeScreen(
                                animatedVisibilityScope = this@composable,
                                navigateToSignIn = { login(this@MainActivity) },
                                navigateToSearch = {
                                    navController.navigate(SearchRoute)
                                },
                                navigateToDetails = { image ->
                                    navController.navigate(ImageDetailRoute(image.id, image.link))
                                }
                            )
                        }
                        composable<ImageDetailRoute> { backStackEntry ->
                            val path: ImageDetailRoute = backStackEntry.toRoute()
                            ImageDetailScreen(
                                navController = navController,
                                imageId = path.imageId,
                                link = path.link,
                                modifier = Modifier.sharedElement(rememberSharedContentState(key = path.imageId), this)
                            )
                        }
                        composable<SearchRoute> {
                            SearchScreen({navController.popBackStack(route = HomeRoute, inclusive = false) })
                        }
//                        composable(
//                            route = "loginCallback?access_token={access_token}&refresh_token={refresh_token}&account_username={username}&expires_in={expires_in}",
//                            arguments = listOf(
//                                navArgument("access_token") { type = NavType.StringType },
//                                navArgument("refresh_token") { type = NavType.StringType },
//                                navArgument("expires_in") { type = NavType.LongType },
//                                navArgument("account_username") {
//                                    type = NavType.StringType
//                                    nullable = true
//                                },
//                            ),
//                            deepLinks = listOf(navDeepLink {
//                                uriPattern =
//                                    "verdant://logincallback?access_token={access_token}&refresh_token={refresh_token}&account_username={username}&expires_in={expires_in}"
//                            })
//                        ) { backStackEntry ->
//                            val token = backStackEntry.arguments?.getString("token")
//                            WelcomeScreen(token)
//                        }
                    }
                }
            }
        }
    }
}

/**
 * @author frankegan created on 10/24/15.
 */
private val IMGUR_CLIENT_ID = BuildConfig.IMGUR_CLIENT_ID
val LOGIN_URL = "https://api.imgur.com/oauth2/authorize?client_id=$IMGUR_CLIENT_ID&response_type=token"

/**
 * Calling this method will initiate a login flow hat end with the user either logging in or declining.
 *
 * @param host    The host activity you are calling from.
 * @param session The CustomTabSession, this is only useful you were planning on warming up tab or something like that.
 */
private fun login(host: Activity) {
    val color = ContextCompat.getColor(host, R.color.material_lightgreen500)

    val colorParams = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(color)
        .build()

    // Build CustomTabsIntent
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true) // Show title in toolbar
        .setInstantAppsEnabled(true)
        .setDefaultColorSchemeParams(colorParams)
        .build()

    // Launch the URL
    Log.d("login", LOGIN_URL)
    customTabsIntent.launchUrl(host, LOGIN_URL.toUri())
}