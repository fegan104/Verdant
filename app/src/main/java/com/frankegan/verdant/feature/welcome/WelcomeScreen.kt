package com.frankegan.verdant.feature.welcome

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WelcomeScreen(token: String?, viewModel: WelcomeViewModel = viewModel()) {
    LaunchedEffect(token) {
        viewModel.saveUser(token.orEmpty())
    }
    Text(token.toString())
}