package com.jeffcamp.android.restaurantdiscoverycompose.repository

import com.google.android.gms.maps.model.LatLng
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.PlaceDetail
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.PlaceDetailsResult
import com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.NetworkDataSource
import com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model.PlaceDetailsResponse

class PlaceDetailsRepository(private val networkDataSource: NetworkDataSource) {

    suspend fun getPlaceDetail(placeId: String): PlaceDetailsResult {
        return networkDataSource.getPlaceDetail(placeId).toDomainModel()
    }

    private fun PlaceDetailsResponse.toDomainModel(): PlaceDetailsResult {
        return result?.let {
            PlaceDetailsResult(
                placeDetail = PlaceDetail(
                    name = it.name,
                    rating = it.rating,
                    ratingsTotal = it.userRatingsTotal,
                    priceLevel = it.priceLevel,
                    isOpenNow = it.openingHours?.openNow
                ),
                formattedAddress = it.formattedAddress,
                formattedPhoneNumber = it.formattedPhoneNumber,
                photos = it.photos,
                website = it.website,
                weekdayText = it.openingHours?.weekdayText.orEmpty(),
                location = LatLng(it.geometry.location.lat, it.geometry.location.lng)
            )
        } ?: throw IllegalStateException("No result returned for place details request")
    }
}