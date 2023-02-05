package com.jeffcamp.android.restaurantdiscoverycompose.domain.model

import com.google.android.gms.maps.model.LatLng
import com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model.Photo

data class PlaceDetailsResult(
    val formattedAddress: String,
    val formattedPhoneNumber: String,
    val placeDetail: PlaceDetail,
    var photos: List<Photo>,
    val website: String,
    val weekdayText: List<String>,
    val location: LatLng
)
