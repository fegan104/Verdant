package com.frankegan.verdant.feature.imagedetail

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.Manifest
import android.R.attr.description
import android.R.attr.onClick
import android.content.pm.PackageManager
import android.os.Build
import android.system.Os
import android.system.Os.link
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.frankegan.verdant.feature.viewer.ImageViewerRoute
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.text.NumberFormat
import androidx.core.net.toUri
import com.frankegan.verdant.data.ImgurImage

@Serializable
data class ImageDetailRoute(val imageId: String, val link: String)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ImageDetailScreen(
    navController: NavController,
    imageId: String,
    link: String,
    modifier: Modifier = Modifier,
) {
    val formatter = remember { NumberFormat.getNumberInstance() }
    val viewModel = viewModel<ImageDetailViewModel>(factory = ImageDetailViewModel.Factory(imageId))
    val imageResult by viewModel.imgurImage.collectAsStateWithLifecycle(null)
    val image = imageResult?.getOrNull()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            downloadImage(
                context = context,
                image = image,
            )
        }
    }

    Scaffold(contentWindowInsets = WindowInsets(0)) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Box {
                AsyncImage(
                    model = link,
                    contentDescription = image?.description,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clickable { navController.navigate(ImageViewerRoute(link)) }
                        .height(420.dp)
                        .fillMaxWidth()
                )

                IconButton(
                    onClick = navController::popBackStack,
                    Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                ) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "back",
                        tint = Color.Black,
                    )
                }
            }

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                image?.title?.let { title ->
                    Text(title, style = MaterialTheme.typography.titleMedium)
                }
                image?.description?.let { description ->
                    Text(description, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                ImageDetail(
                    icon = Icons.Default.RemoveRedEye,
                    contentDescription = "eye",
                    text = formatter.format(image?.views ?: 0),
                )
                ImageDetail(
                    icon = Icons.Default.Download,
                    contentDescription = "down arrow",
                    text = "Download",
                    onClick = {
                        scope.launch {
                            when {
                                needsStoragePermission(context) -> {
                                    launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                }

                                else -> {
                                    downloadImage(context, image)
                                }
                            }
                        }
                    }
                )
                ImageDetail(
                    icon = Icons.Default.Share,
                    contentDescription = "share",
                    text = "Share",
                )
            }
        }
    }
}

@Composable
private fun ImageDetail(
    icon: ImageVector,
    contentDescription: String,
    text: String,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(
                radius = Dp.Unspecified,
                bounded = false
            ),
            onClick = onClick,
        ),
    ) {
        Icon(icon, contentDescription)
        Text(text, style = MaterialTheme.typography.titleSmall)
    }
}

private fun needsStoragePermission(context: Context): Boolean {
    val permissionCheck = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            && (permissionCheck == PackageManager.PERMISSION_DENIED)
}

private fun downloadImage(context: Context, image: ImgurImage?) {
    image ?: return
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(image.link.toUri())
        .setTitle(image.title)
        .setDescription(image.description.orEmpty())
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, image.link.substringAfterLast("/"))
        .setMimeType("image/${image.extension}")
    downloadManager.enqueue(request)
}