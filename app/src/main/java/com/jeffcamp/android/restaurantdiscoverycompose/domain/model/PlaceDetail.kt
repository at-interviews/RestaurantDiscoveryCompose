package com.jeffcamp.android.restaurantdiscoverycompose.domain.model

data class PlaceDetail(
    val name: String,
    val rating: Double,
    val ratingsTotal: Int,
    val priceLevel: Int?,
    val isOpenNow: Boolean?
)