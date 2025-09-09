package com.frankegan.verdant.feature.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.frankegan.verdant.data.ImgurImage
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    login: () -> Unit,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        Button(onClick = login) {
            Text("Sign In")
        }
    }
}

@Composable
private fun HomeScreen(
    images: List<ImgurImage>,
    modifier: Modifier = Modifier,
) {
    Column {
        for (image in images) {
            Card {
                AsyncImage(image.link, contentDescription = image.description)
            }
        }
    }
}