package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpeningHours(
    @SerialName("open_now")
    val openNow: Boolean = false,
    @SerialName("weekday_text")
    val weekdayText: List<String> = emptyList()
)