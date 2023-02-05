package com.jeffcamp.android.restaurantdiscoverycompose.ext

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

val CameraPositionState.currentLocation: LatLng?
    get() {
        return projection?.visibleRegion?.let { visibleRegion ->
            val nearLeft = visibleRegion.nearLeft
            val farRight = visibleRegion.farRight
            LatLng(
                (farRight.latitude + nearLeft.latitude) / 2,
                (nearLeft.longitude + farRight.longitude) / 2
            )
        }
    }