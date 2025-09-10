package com.frankegan.verdant.feature.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.room.util.TableInfo
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.util.DebugLogger
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
    viewModel: HomeViewModel = viewModel()
) {
    val pagingData = viewModel.images.collectAsLazyPagingItems()
    val imageLoader = LocalContext.current.imageLoader.newBuilder()
        .logger(DebugLogger())
        .build()

    with(sharedTransitionScope) {
        Column {
            Button(onClick = login) {
                Text("Sign In")
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(pagingData.itemSnapshotList) { article ->
                    if (article != null) {

                        Card {
                            AsyncImage(
                                model = article.link,
                                contentDescription = article.title,
                                imageLoader = imageLoader,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = article.title,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // Optional: handle loading state
                pagingData.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { Text("Refreshing…") }
                        }

                        loadState.append is LoadState.Loading -> {
                            item { Text("Loading more…") }
                        }

                        loadState.append is LoadState.Error -> {
                            val e = loadState.append as LoadState.Error
                            item {
                                Text("Error: ${e.error.localizedMessage}")
                            }
                        }
                    }
                }
            }
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