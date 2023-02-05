package com.jeffcamp.android.restaurantdiscoverycompose.domain.model

import com.google.android.gms.maps.model.LatLng

data class NearbySearchResult(
    val placeDetail: PlaceDetail,
    val placeId: String,
    val location: LatLng,
    var photoReference: String,
    val businessStatus: String
)
