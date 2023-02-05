package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacesNearbySearchResponse(
    @SerialName("html_attributions")
    val htmlAttributions: List<String> = listOf(),
    @SerialName("next_page_token")
    val nextPageToken: String = "",
    @SerialName("results")
    val results: List<Result> = listOf(),
    @SerialName("status")
    val status: String = ""
)