package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailsResponse(
    @SerialName("html_attributions")
    val htmlAttributions: List<String> = listOf(),
    @SerialName("result")
    val result: DetailsResult? = null,
    @SerialName("status")
    val status: String = ""
)