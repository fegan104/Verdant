package com.frankegan.verdant.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SubredditModel {

    private val _currentSubreddit = MutableStateFlow("earthporn")
    val currentSubreddit: StateFlow<String> = _currentSubreddit

    fun updateSubreddit(next: String) {
        _currentSubreddit.value = next
    }
}