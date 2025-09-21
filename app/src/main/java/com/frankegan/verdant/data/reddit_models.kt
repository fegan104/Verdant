package com.frankegan.verdant.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubredditSearchResponse(
    val data: SearchResponseData
)

@Serializable
data class SearchResponseData(
    val children: List<SearchResponseItem>
)

@Serializable
data class SearchResponseItem(
    val data: SubredditInfo
)

@Serializable
data class SubredditInfo(
    val title: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("icon_img")
    val iconImg: String?
)
