package com.frankegan.verdant.feature.home

import androidx.lifecycle.ViewModel
import com.frankegan.verdant.data.ImgurDataSource
import com.frankegan.verdant.data.ImgurRepository

class HomeViewModel: ViewModel() {
    val repo: ImgurDataSource = ImgurRepository.getInstance()


}