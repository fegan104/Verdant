package com.frankegan.verdant.feature.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Serializable
data class ImageViewerRoute(val imageLink: String)

@Composable
fun ImageViewerScreen(imageLink: String) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = imageLink,
            contentDescription = null,
            Modifier.fillMaxSize().zoomable(rememberZoomState())
        )
    }
}