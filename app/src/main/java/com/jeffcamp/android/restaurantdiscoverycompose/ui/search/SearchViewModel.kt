package com.jeffcamp.android.restaurantdiscoverycompose.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.NearbySearchResult
import com.jeffcamp.android.restaurantdiscoverycompose.repository.SearchRepository
import io.ktor.serialization.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Duration.Companion.seconds

private const val BUSINESS_STATUS_OPERATIONAL = "OPERATIONAL"
class SearchViewModel(
    private val locationClient: FusedLocationProviderClient,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val allTrailsLocation = LatLng(37.7908321, -122.410566)

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: Flow<LatLng?>
        get() = _location

    private val _searchResults = MutableStateFlow<List<NearbySearchResult>>(emptyList())
    val searchResults: Flow<List<NearbySearchResult>>
        get() = _searchResults

    private val _searchStatus = MutableStateFlow(SearchStatus.NONE)
    val searchStatus: Flow<SearchStatus>
        get() = _searchStatus

    private val _displayMode = MutableStateFlow(DisplayMode.MAP)
    val displayMode: Flow<DisplayMode>
        get() = _displayMode

    private val supervisorJob = SupervisorJob()

    @SuppressLint("MissingPermission")
    fun onLocationPermissionGranted() {
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                _location.value = LatLng(location.latitude, location.longitude).also {
                    getNearbyRestaurants(it)
                }
            }
    }

    fun onLocationPermissionDenied() {
        _location.value = allTrailsLocation
        getNearbyRestaurants(allTrailsLocation)
    }

    fun setLocation(location: LatLng) {
        _location.value = location
    }

    fun onDisplayModeUpdated(displayMode: DisplayMode) {
        _displayMode.value = displayMode
    }

    fun performSearch(queryString: String) {
        supervisorJob.cancelChildren()
        val job = Job(parent = supervisorJob)
        _searchStatus.value = SearchStatus.IN_PROGRESS
        viewModelScope.launch(job) {
            runCatching {
                _location.value?.let { latLng ->
                    val response = searchRepository.getRestaurantsForQuery(latLng, queryString)
                    _searchResults.value = response.results.filter { it.businessStatus == BUSINESS_STATUS_OPERATIONAL }
                    _searchStatus.value = SearchStatus.COMPLETED_WITH_RESULTS
                    getNextPage(latLng, response.nextPageToken)
                } ?: run {
                    _searchStatus.value = SearchStatus.COMPLETED_NO_RESULTS
                }
            }.onFailure {
                _searchStatus.value = SearchStatus.FAILED
            }
        }
    }

    private fun getNearbyRestaurants(latLng: LatLng) {
        supervisorJob.cancelChildren()
        val job = Job(parent = supervisorJob)
        viewModelScope.launch(job) {
            runCatching {
                val response = searchRepository.getNearbyRestaurants(latLng)
                _searchResults.value = response.results.filter { it.businessStatus == BUSINESS_STATUS_OPERATIONAL }
                getNextPage(latLng, response.nextPageToken)
            }
        }
    }

    private suspend fun getNextPage(latLng: LatLng, nextPageToken: String) {
        var token = nextPageToken
        while (token.isNotBlank()) {
            delay(2.seconds)
            val nextPage = searchRepository.getNextPage(latLng, token)
            _searchResults.value += nextPage.results.filter { it.businessStatus == BUSINESS_STATUS_OPERATIONAL }
            token = nextPage.nextPageToken
        }
    }

    companion object {
        fun provideFactory(
            locationClient: FusedLocationProviderClient,
            searchRepository: SearchRepository,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return SearchViewModel(locationClient, searchRepository) as T
                }
            }
    }
}