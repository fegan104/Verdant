package com.frankegan.verdant.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.frankegan.verdant.data.ImgurRepository
import com.frankegan.verdant.data.local.SubredditModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel: ViewModel() {
    val repo = ImgurRepository.getInstance()

    private val currentSubreddit = SubredditModel.currentSubreddit
    val images = currentSubreddit.flatMapLatest { subreddit ->
        repo.observeImagePaging(subreddit).cachedIn(viewModelScope)
    }
}