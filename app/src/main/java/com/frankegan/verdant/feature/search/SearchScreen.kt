package com.frankegan.verdant.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.frankegan.verdant.data.SubredditInfo
import com.frankegan.verdant.data.local.SubredditModel
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(),
) {
    var query by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val searchBarPadding = if (isExpanded) 0.dp else 16.dp
            val searchResponse by viewModel.observeResponse(query).collectAsStateWithLifecycle(emptyList())

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = searchBarPadding)) {
                if (!isExpanded) {
                    IconButton(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .statusBarsPadding(),
                        onClick = navigateBack,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                }
                SearchBar(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(end = searchBarPadding),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = query,
                            onQueryChange = { query = it },
                            onSearch = { },
                            expanded = isExpanded,
                            onExpandedChange = { isExpanded = it },
                            placeholder = {
                                Text("Search for subreddit galleries")
                            }
                        )
                    },
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it },
                    content = {
                        if (query.isEmpty()) {
                            EmptyState()
                        } else {
                            LazyColumn(Modifier.fillMaxSize()) {
                                items(searchResponse) { subredditInfo ->
                                    SubredditRow(subredditInfo, Modifier.clickable {
                                        SubredditModel.updateSubreddit(subredditInfo.displayName)
                                        navigateBack()
                                    })
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        EmptyState(Modifier.padding(innerPadding))
    }
}

@Composable
fun SubredditRow(info: SubredditInfo, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Subreddit icon (if present)
        if (info.iconImg.isNotEmpty()) {
            AsyncImage(
                model = info.iconImg,
                contentDescription = "${info.displayName} icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column {
            Text(
                text = "r/${info.displayName}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = info.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = BiasAlignment(0f, -0.3f)) {
        Text("Find a new gallery", style = MaterialTheme.typography.headlineLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun Search_Preview(modifier: Modifier = Modifier) {
    SearchScreen({})
}