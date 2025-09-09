package com.frankegan.verdant.feature.imagedetail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.frankegan.verdant.data.ImgurImage
import kotlinx.serialization.Serializable

@Serializable
data class ImageDetailRoute(val image: ImgurImage)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ImageDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {

    }
}