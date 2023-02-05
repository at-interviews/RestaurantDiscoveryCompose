package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Close(
    @SerialName("day")
    val day: Int = 0,
    @SerialName("time")
    val time: String = ""
)