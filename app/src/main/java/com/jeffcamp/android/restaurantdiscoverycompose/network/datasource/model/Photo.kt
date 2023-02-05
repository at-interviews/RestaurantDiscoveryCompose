package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    @SerialName("height")
    val height: Int = 0,
    @SerialName("html_attributions")
    val htmlAttributions: List<String> = listOf(),
    @SerialName("photo_reference")
    val photoReference: String = "",
    @SerialName("width")
    val width: Int = 0
)