package com.frankegan.verdant.feature.home

import android.R.attr.onClick
import android.graphics.ImageDecoder
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.palette.graphics.Palette
import coil3.Bitmap
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePainter.State.Empty.painter
import coil3.compose.rememberAsyncImagePainter
import coil3.imageLoader
import coil3.toBitmap
import coil3.util.CoilUtils.result
import coil3.util.DebugLogger
import com.frankegan.verdant.data.ImgurImage
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okio.`-DeprecatedOkio`.source

@Serializable
data object HomeRoute

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navigateToDetails: (ImgurImage) -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = viewModel()
) {
    val pagingData = viewModel.images.collectAsLazyPagingItems()

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
                    IconButton(navigateToSearch) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            Modifier.size(30.dp),
                        )
                    }
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
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            val loadState  = pagingData.loadState
            when {
                loadState.append is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    Text("Error: ${e.error.localizedMessage}", color = MaterialTheme.colorScheme.error)
                }
                loadState.refresh is LoadState.Error -> {
                    val e = loadState.refresh as LoadState.Error
                    Text("Error: ${e.error.localizedMessage}", color = MaterialTheme.colorScheme.error)
                }
            }

            PullToRefreshBox(
                isRefreshing = (pagingData.loadState.append is LoadState.Loading || pagingData.loadState.refresh is LoadState.Loading),
                onRefresh = { pagingData.refresh() },
                modifier = modifier
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(pagingData.itemCount, key = { pagingData[it]?.id!! }) { index ->
                        pagingData[index]?.let { image ->
                            ImageItem(image, animatedVisibilityScope, navigateToDetails)
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
    image: ImgurImage,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigateToDetails: (ImgurImage) -> Unit,
) {
    val painter = rememberAsyncImagePainter(model = image.link)
    val imageState by painter.state.collectAsStateWithLifecycle(null)
    val uiScope = rememberCoroutineScope()
    var cardColor by remember { mutableStateOf(Color.Unspecified) }
    var titleColor by remember { mutableStateOf(Color.Unspecified) }

    LaunchedEffect(imageState) {
        val imageState = imageState
        if (imageState is AsyncImagePainter.State.Success) {
            val source = imageState.result.image.toBitmap()
            val bitmap = source.copy(android.graphics.Bitmap.Config.ARGB_8888, true)

            uiScope.launch {
                Palette.from(bitmap).generate().vibrantSwatch?.let { swatch ->
                    cardColor = Color(swatch.rgb)
                    titleColor = Color(swatch.bodyTextColor)
                }
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.sharedElement(rememberSharedContentState(key = image.id), animatedVisibilityScope),
        onClick = { navigateToDetails(image) }
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            painter = painter,
            contentDescription = image.title,
            contentScale = ContentScale.Crop,
        )
        Text(
            text = image.title,
            modifier = Modifier.padding(16.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = titleColor,
        )
    }
}