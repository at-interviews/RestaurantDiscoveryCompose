package com.jeffcamp.android.restaurantdiscoverycompose.repository

import com.google.android.gms.maps.model.LatLng
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.NearbySearchResult
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.NearbySearchResults
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.PlaceDetail
import com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.NetworkDataSource
import com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model.PlacesNearbySearchResponse

class SearchRepository(private val networkDataSource: NetworkDataSource) {

    suspend fun getNearbyRestaurants(latLng: LatLng): NearbySearchResults {
        return networkDataSource.getNearbyRestaurants(latLng).toDomainModel()
    }

    suspend fun getRestaurantsForQuery(latLng: LatLng, queryString: String): NearbySearchResults {
        return networkDataSource.getRestaurantsForQuery(latLng, queryString).toDomainModel()
    }

    suspend fun getNextPage(latLng: LatLng, nextPageToken: String): NearbySearchResults {
        return networkDataSource.getRestaurantsNextPage(latLng, nextPageToken).toDomainModel()
    }

    private fun PlacesNearbySearchResponse.toDomainModel(): NearbySearchResults {
        return NearbySearchResults(
            results = results.map {
                NearbySearchResult(
                    placeDetail = PlaceDetail(
                        name = it.name,
                        rating = it.rating,
                        ratingsTotal = it.userRatingsTotal,
                        priceLevel = it.priceLevel,
                        isOpenNow = it.openingHours?.openNow
                    ),
                    placeId = it.placeId,
                    location = LatLng(it.geometry.location.lat, it.geometry.location.lng),
                    photoReference = if (it.photos.isNotEmpty()) it.photos[0].photoReference else "",
                    businessStatus = it.businessStatus
                )
            },
            nextPageToken = nextPageToken
        )
    }
}