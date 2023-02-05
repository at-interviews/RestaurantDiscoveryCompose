package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailsResult(
    @SerialName("business_status")
    val businessStatus: String = "",
    @SerialName("formatted_address")
    val formattedAddress: String = "",
    @SerialName("formatted_phone_number")
    val formattedPhoneNumber: String = "",
    @SerialName("geometry")
    val geometry: Geometry = Geometry(),
    @SerialName("icon")
    val icon: String = "",
    @SerialName("icon_background_color")
    val iconBackgroundColor: String = "",
    @SerialName("icon_mask_base_uri")
    val iconMaskBaseUri: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("opening_hours")
    val openingHours: OpeningHours? = null,
    @SerialName("permanently_closed")
    val permanentlyClosed: Boolean? = null,
    @SerialName("photos")
    val photos: List<Photo> = listOf(),
    @SerialName("place_id")
    val placeId: String = "",
    @SerialName("plus_code")
    val plusCode: PlusCode = PlusCode(),
    @SerialName("price_level")
    val priceLevel: Int? = null,
    @SerialName("rating")
    val rating: Double = 0.0,
    @SerialName("reference")
    val reference: String = "",
    @SerialName("scope")
    val scope: String = "",
    @SerialName("types")
    val types: List<String> = listOf(),
    @SerialName("user_ratings_total")
    val userRatingsTotal: Int = 0,
    @SerialName("vicinity")
    val vicinity: String = "",
    @SerialName("website")
    val website: String = "",
)