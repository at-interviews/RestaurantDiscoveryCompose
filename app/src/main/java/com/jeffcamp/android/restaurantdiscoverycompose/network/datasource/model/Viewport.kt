package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Viewport(
    @SerialName("northeast")
    val northeast: Northeast = Northeast(),
    @SerialName("southwest")
    val southwest: Southwest = Southwest()
)