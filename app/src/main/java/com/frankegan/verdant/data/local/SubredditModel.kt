package com.frankegan.verdant.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

object SubredditModel {

    private val _currentSubreddit = MutableStateFlow("earthporn")
    val currentSubreddit: Flow<String> = _currentSubreddit

    fun updateSubreddit(next: String) {
        _currentSubreddit.value = next
    }
}