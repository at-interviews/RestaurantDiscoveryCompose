package com.jeffcamp.android.restaurantdiscoverycompose.ext

import com.jeffcamp.android.restaurantdiscoverycompose.app.TheApplication

fun String.getPhotoReferenceUrl(maxSize: Int = 400): String? {
    return if (this.isNotBlank()) {
        TheApplication.application?.googleApiKey?.let { apiKey ->
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxSize&photo_reference=$this&key=$apiKey"
        }
    } else {
        null
    }
}