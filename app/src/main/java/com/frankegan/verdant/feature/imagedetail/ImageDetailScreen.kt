package com.frankegan.verdant.feature.imagedetail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.frankegan.verdant.data.ImgurImage
import kotlinx.serialization.Serializable

@Serializable
data class ImageDetailRoute(val imageId: String, val link: String)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ImageDetailScreen(
    imageId: String,
    link: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<ImageDetailViewModel>(factory = ImageDetailViewModel.Factory(imageId))
    val imageResult by viewModel.imgurImage.collectAsStateWithLifecycle(null)
    val title = imageResult?.getOrNull()?.title.orEmpty()

    Column(modifier) {
        AsyncImage(
            model = link,
            contentDescription = title,
        )

        Text(title, style = MaterialTheme.typography.titleLarge)
    }
}