package com.frankegan.verdant.feature.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.util.DebugLogger
import com.frankegan.verdant.data.ImgurImage
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navigateToDetails: (ImgurImage) -> Unit,
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = viewModel()
) {
    var input by remember { mutableStateOf("") }
    val pagingData = viewModel.images.collectAsLazyPagingItems()
    val imageLoader = LocalContext.current.imageLoader.newBuilder()
        .logger(DebugLogger())
        .build()

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                title = {
                    Text("Verdant")
                },
                actions = {
                    IconButton(navigateToSignIn) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            Modifier
                                .padding(horizontal = 4.dp)
                                .size(40.dp),
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            TextField(
                input,
                onValueChange = { input = it },
                modifier = Modifier.imePadding(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.updateSubreddit(input) })
            )
//            FloatingActionButton(onClick = {}, Modifier.padding(bottom = 32.dp)) {
//                Icon(Icons.Default.Search, contentDescription = "Subreddit")
//            }
        }
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            PullToRefreshBox(
                isRefreshing = !pagingData.loadState.isIdle,
                onRefresh = {
                    pagingData.refresh()
                },
                modifier = modifier
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(pagingData.itemSnapshotList, key = { it?.id.orEmpty() }) { article ->
                        if (article != null) {
                            ImageItem(article, animatedVisibilityScope, navigateToDetails, imageLoader)
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
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ImageItem(
    article: ImgurImage,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigateToDetails: (ImgurImage) -> Unit,
    imageLoader: ImageLoader
) {
    Card(
        modifier = Modifier.sharedElement(rememberSharedContentState(key = article.id), animatedVisibilityScope),
        onClick = {
            navigateToDetails(article)
        }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            model = article.link,
            contentDescription = article.title,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop
        )
        Text(
            text = article.title,
            modifier = Modifier.padding(16.dp)
        )
    }
}