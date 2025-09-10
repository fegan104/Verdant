package com.frankegan.verdant.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.frankegan.verdant.data.ImgurRepository

class HomeViewModel: ViewModel() {
    val repo = ImgurRepository.getInstance()

    val images = repo.getImages().cachedIn(viewModelScope)

}