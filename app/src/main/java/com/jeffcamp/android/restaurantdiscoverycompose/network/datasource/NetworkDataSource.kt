package com.jeffcamp.android.restaurantdiscoverycompose.network.datasource

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.jeffcamp.android.restaurantdiscoverycompose.app.TheApplication
import com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model.PlaceDetailsResponse
import com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.model.PlacesNearbySearchResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private const val AND = "&"
private const val COMMA = "%2C"

private const val NEARBY_SEARCH_RADIUS_METERS = 1609 * 10 // approx 10 miles
private const val BASE_NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
private const val BASE_PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?"

private const val TYPE_QUERY_STRING = "type=restaurant"

class NetworkDataSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val placeDetailsQueryString =
        StringBuilder()
            .append("fields=name")
            .append(COMMA)
            .append("business_status")
            .append(COMMA)
            .append("formatted_address")
            .append(COMMA)
            .append("photo")
            .append(COMMA)
            .append("rating")
            .append(COMMA)
            .append("user_ratings_total")
            .append(COMMA)
            .append(("opening_hours"))
            .append(COMMA)
            .append(("price_level"))
            .append(COMMA)
            .append(("formatted_phone_number"))
            .append(COMMA)
            .append(("website"))
            .append(COMMA)
            .append(("geometry"))

    private val LatLng.locationQueryString: String
        get() = "location=$latitude%2C$longitude"

    private val Int.radiusQueryString: String
        get() = "radius=$this"

    private val keyQueryString: String
        get() = "key=${TheApplication.application?.googleApiKey}"

    private val String.keywordQueryString: String
        get() = "keyword=$this"

    private val String.nextPageQueryString: String
        get() = "pagetoken=$this"

    private val String.placeIdQueryString: String
        get() = "place_id=$this"

    private val client = HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("NetworkDataSource", message)
                }

            }
        }
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun getNearbyRestaurants(latLng: LatLng): PlacesNearbySearchResponse = withContext(dispatcher) {
        client.get(getNearbyRestaurantsUrl(latLng)).body()
    }

    suspend fun getRestaurantsForQuery(
        latLng: LatLng,
        queryString: String
    ): PlacesNearbySearchResponse = withContext(dispatcher) {
        client.get(getNearbyRestaurantsUrl(latLng, queryString)).body()
    }

    suspend fun getRestaurantsNextPage(latLng: LatLng, nextPageToken: String): PlacesNearbySearchResponse = withContext(dispatcher) {
        client.get(getRestaurantsNextPageUrl(latLng, nextPageToken)).body()
    }

    suspend fun getPlaceDetail(placeId: String): PlaceDetailsResponse = withContext(dispatcher) {
        client.get(getPlaceDetailsUrl(placeId)).body()
    }

    private fun getNearbyRestaurantsUrl(latLng: LatLng, queryString: String? = null): String {
        val stringBuilder = StringBuilder()
        stringBuilder
            .append(BASE_NEARBY_SEARCH_URL)
            .append(latLng.locationQueryString)
            .append(AND)
            .append(NEARBY_SEARCH_RADIUS_METERS.radiusQueryString)
            .append(AND)
            .append(TYPE_QUERY_STRING)
            .append(AND)
            .append(keyQueryString)
        queryString?.let { keyword ->
            stringBuilder.append("&").append(keyword.keywordQueryString)
        }
        return stringBuilder.toString()
    }

    private fun getRestaurantsNextPageUrl(latLng: LatLng, nextPageToken: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder
            .append(BASE_NEARBY_SEARCH_URL)
            .append(latLng.locationQueryString)
            .append(AND)
            .append(NEARBY_SEARCH_RADIUS_METERS)
            .append(AND)
            .append(nextPageToken.nextPageQueryString)
            .append(AND)
            .append(keyQueryString)
        return stringBuilder.toString()
    }

    private fun getPlaceDetailsUrl(placeId: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder
            .append(BASE_PLACE_DETAILS_URL)
            .append(placeId.placeIdQueryString)
            .append(AND)
            .append(placeDetailsQueryString)
            .append(AND)
            .append(keyQueryString)
        return stringBuilder.toString()
    }
}
