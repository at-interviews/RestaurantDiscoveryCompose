@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalGlideComposeApi::class)

package com.jeffcamp.android.restaurantdiscoverycompose.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.jeffcamp.android.restaurantdiscoverycompose.R
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.NearbySearchResult
import com.jeffcamp.android.restaurantdiscoverycompose.ext.currentLocation
import com.jeffcamp.android.restaurantdiscoverycompose.ext.getPhotoReferenceUrl
import com.jeffcamp.android.restaurantdiscoverycompose.ext.getVectorBitmapDescriptor
import com.jeffcamp.android.restaurantdiscoverycompose.ui.common.BasicRestaurantInfo
import com.jeffcamp.android.restaurantdiscoverycompose.ui.common.ToggleViewButton
import com.jeffcamp.android.restaurantdiscoverycompose.ui.theme.Transparent
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onPlaceSelected: (placeId: String) -> Unit
) {
    val displayMode by viewModel.displayMode.collectAsState(initial = DisplayMode.MAP)
    val location by viewModel.location.collectAsState(initial = null)

    Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)) {
        Header()
        location?.let { latLng ->
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(latLng, 12f)
            }
            SearchBar(viewModel, cameraPositionState)
            if (displayMode == DisplayMode.MAP) {
                MapView(viewModel, cameraPositionState, onPlaceSelected)
            } else {
                ResultsList(viewModel, onPlaceSelected)
            }
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp)
            .height(24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Image(
            painterResource(id = R.drawable.ic_logo),
            contentDescription = stringResource(R.string.content_description_logo)
        )
        Spacer(modifier = Modifier.width(3.5.dp))
        Image(
            painterResource(id = R.drawable.ic_alltrails),
            contentDescription = stringResource(R.string.content_description_alltrails)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.text_at_lunch),
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun SearchBar(viewModel: SearchViewModel, cameraPositionState: CameraPositionState) {
    val interactionSource = remember { MutableInteractionSource() }
    var value by remember { mutableStateOf("") }
    val searchStatus by viewModel.searchStatus.collectAsState(initial = SearchStatus.NONE)
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = value,
        onValueChange = { value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                cameraPositionState.currentLocation?.let { location ->
                    viewModel.setLocation(location)
                }
                viewModel.performSearch(value)
            }
        ),
        singleLine = true
    ) {
        TextFieldDefaults.TextFieldDecorationBox(
            value = "",
            innerTextField = it,
            singleLine = true,
            enabled = searchStatus != SearchStatus.IN_PROGRESS,
            visualTransformation = VisualTransformation.None,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                placeholderColor = MaterialTheme.colorScheme.onTertiary,
                focusedIndicatorColor = Transparent,
                unfocusedIndicatorColor = Transparent,
                disabledIndicatorColor = Transparent
            ),
            placeholder = {
                if (value.isEmpty()) {
                    Text(text = stringResource(R.string.text_search_restaurants), style = MaterialTheme.typography.labelMedium)
                }
                          },
            shape = RoundedCornerShape(40.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(R.string.content_description_search)
                )
                          },
            trailingIcon = {
                if (searchStatus == SearchStatus.IN_PROGRESS) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else if (value.isNotEmpty()) {
                    IconButton(onClick = { value = "" }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = stringResource(id = R.string.content_description_clear)
                        )
                    }
                }
            },
            interactionSource = interactionSource,
            contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                top = 6.dp, bottom = 6.dp
            )
        )
    }
}

@Composable
fun MapView(viewModel: SearchViewModel, cameraPositionState: CameraPositionState, onPlaceSelected: (placeId: String) -> Unit) {
    val searchResults by viewModel.searchResults.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var currentSelectedPin by remember {
        mutableStateOf("")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            onMapLoaded = {
                cameraPositionState.projection?.visibleRegion?.latLngBounds
            }
        ) {
            val selectedPin = LocalContext.current.getVectorBitmapDescriptor(R.drawable.ic_pin_selected)
            val unselectedPin = LocalContext.current.getVectorBitmapDescriptor(R.drawable.ic_pin_unselected)
            searchResults.forEach { result ->
                MarkerInfoWindow(
                    state = MarkerState(position = result.location),
                    icon = if (currentSelectedPin == result.placeId) selectedPin else unselectedPin,
                    onClick = { marker ->
                        currentSelectedPin = result.placeId
                        marker.setIcon(selectedPin)
                        false
                              },
                    onInfoWindowClick = {
                        coroutineScope.launch {
                            onPlaceSelected.invoke(result.placeId)
                        }
                    },
                    onInfoWindowClose = { marker ->
                        currentSelectedPin = ""
                        marker.setIcon(unselectedPin)
                    }
                ) {
                    Box(modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 24.dp, end = 24.dp)) {
                        // TODO: fix bug loading image first time MarkerInfoWindow is shown for a place
                        //  appears to be an issue with compose maps and not glide or coil
                        SearchResultCard(result)
                    }
                }
            }
        }

        if (searchResults.isNotEmpty()) {
            ToggleViewButton(
                currentDisplayMode = DisplayMode.MAP
            ) {
                viewModel.onDisplayModeUpdated(DisplayMode.LIST)
            }
        }
    }
}

@Composable
fun ResultsList(viewModel: SearchViewModel, onPlaceSelected: (placeId: String) -> Unit) {
    val searchResults by viewModel.searchResults.collectAsState(initial = emptyList())

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.tertiary)) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(searchResults) { result ->
                SearchResultCard(result, onPlaceSelected)
            }
        }
        ToggleViewButton(
            currentDisplayMode = DisplayMode.LIST
        ) {
            viewModel.onDisplayModeUpdated(DisplayMode.MAP)
        }
    }
}

@Composable
fun SearchResultCard(result: NearbySearchResult, onPlaceSelected: (placeId: String) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onPlaceSelected.invoke(result.placeId) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(Modifier.padding(16.dp)) {
            val photoUrl = result.photoReference.getPhotoReferenceUrl()
            photoUrl?.let {
                GlideImage(
                    modifier = Modifier.size(64.dp),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    model = photoUrl,
                    contentDescription = stringResource(R.string.content_description_restaurant_thumbnail),
                )
            } ?: Image(
                painterResource(id = R.drawable.ic_no_image),
                contentDescription = stringResource(R.string.content_description_no_image),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(12.5.dp))

            BasicRestaurantInfo(placeDetail = result.placeDetail)
        }
    }
}
