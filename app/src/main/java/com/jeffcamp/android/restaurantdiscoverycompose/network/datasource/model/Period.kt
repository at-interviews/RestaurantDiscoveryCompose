package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Period(
    @SerialName("close")
    val close: Close = Close(),
    @SerialName("open")
    val `open`: Open = Open()
)