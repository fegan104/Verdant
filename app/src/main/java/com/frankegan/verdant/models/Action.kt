package com.frankegan.verdant.models

/**
 * Created by frankegan on 3/19/18.
 */
sealed class Action

data class ToggleFavorite(val id: String) : Action()