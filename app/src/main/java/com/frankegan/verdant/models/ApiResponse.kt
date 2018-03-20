package com.frankegan.verdant.models

/**
 * Created by frankegan on 3/16/18.
 */
data class ApiResponse<T>(val success: Boolean, val status: Int, val data: T)
