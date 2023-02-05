package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressComponent(
    @SerialName("long_name")
    val longName: String = "",
    @SerialName("short_name")
    val shortName: String = "",
    @SerialName("types")
    val types: List<String> = listOf()
)