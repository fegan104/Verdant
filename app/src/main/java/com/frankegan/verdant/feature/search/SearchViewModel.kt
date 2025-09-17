package com.frankegan.verdant.feature.search

import androidx.lifecycle.ViewModel
import com.frankegan.verdant.data.RedditSearchRepository
import kotlinx.coroutines.flow.flow

class SearchViewModel: ViewModel() {
    val repo = RedditSearchRepository.getInstance()

    fun observeResponse(query: String) = flow {
        emit(repo.searchGalleries(query))
    }
}