package com.jeffcamp.android.restaurantdiscoverycompose.ui.details

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.PlaceDetailsResult
import com.jeffcamp.android.restaurantdiscoverycompose.repository.PlaceDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(private val placeDetailsRepository: PlaceDetailsRepository): ViewModel() {

    private val _placeDetails = MutableStateFlow<PlaceDetailsResult?>(null)
    val placeDetails: Flow<PlaceDetailsResult?>
        get() = _placeDetails

    private val _requestStatus = MutableStateFlow(RequestStatus.NONE)
    val requestStatus: Flow<RequestStatus>
        get() = _requestStatus

    fun getPlaceDetails(placeId: String) {
        _requestStatus.value = RequestStatus.IN_PROGRESS
        viewModelScope.launch {
            runCatching {
                placeDetailsRepository.getPlaceDetail(placeId)
            }.onSuccess {
                _placeDetails.value = it
                _requestStatus.value = RequestStatus.SUCCESS
            }.onFailure {
                _requestStatus.value = RequestStatus.SUCCESS
            }
        }
    }

    companion object {
        fun provideFactory(
            placeDetailsRepository: PlaceDetailsRepository,
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
                    return DetailsViewModel(placeDetailsRepository) as T
                }
            }
    }
}