@file:OptIn(ExperimentalGlideComposeApi::class)

package com.jeffcamp.android.restaurantdiscoverycompose.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jeffcamp.android.restaurantdiscoverycompose.R
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.PlaceDetailsResult
import com.jeffcamp.android.restaurantdiscoverycompose.ext.getPhotoReferenceUrl
import com.jeffcamp.android.restaurantdiscoverycompose.ui.common.BasicRestaurantInfo
import com.jeffcamp.android.restaurantdiscoverycompose.ui.common.RestaurantInfoRow

@Composable
fun DetailsScreen(viewModel: DetailsViewModel, placeId: String) {
    val placeDetails by viewModel.placeDetails.collectAsState(initial = null)
    val requestStatus by viewModel.requestStatus.collectAsState(initial = RequestStatus.NONE)

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.primary)) {
        when (requestStatus) {
            RequestStatus.NONE -> viewModel.getPlaceDetails(placeId)
            RequestStatus.IN_PROGRESS -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 4.dp
                )
            }
            RequestStatus.SUCCESS -> {
                DetailsResultsScreen(placeDetails = placeDetails)
            }
            RequestStatus.ERROR -> {
                DetailsErrorScreen()
            }
        }
    }
}

@Composable
fun DetailsResultsScreen(placeDetails: PlaceDetailsResult?) {
    placeDetails?.let { details ->
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))

            BasicRestaurantInfo(placeDetail = details.placeDetail, modifier = Modifier.padding(horizontal = 24.dp))

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(start = 24.dp),
            ) {
                val photos = details.photos.map { it.photoReference }
                items(photos) { photoReference ->
                    GlideImage(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        model = photoReference.getPhotoReferenceUrl(maxSize = 768),
                        contentDescription = stringResource(R.string.content_description_restaurant_image),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            RestaurantInfoRow(
                text = details.formattedAddress,
                drawableResId = R.drawable.ic_location,
                contentDescription = stringResource(R.string.content_description_address),
                uriString = "geo:${details.location.latitude},${details.location.longitude}",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
            )

            RestaurantInfoRow(
                text = details.formattedPhoneNumber,
                drawableResId = R.drawable.ic_phone,
                contentDescription = stringResource(R.string.content_description_phone_number),
                uriString = "tel:${details.formattedPhoneNumber}",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                showTopLine = false
            )

            RestaurantInfoRow(
                text = details.website,
                drawableResId = R.drawable.ic_surfing,
                contentDescription = stringResource(R.string.content_description_website),
                uriString = details.website,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                showTopLine = false
            )

            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painterResource(id = R.drawable.ic_time),
                    contentDescription = stringResource(R.string.content_description_hours)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    details.weekdayText.forEach {
                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = it,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
        }
    }
}

@Composable
fun BoxScope.DetailsErrorScreen() {
    Text(
        text = stringResource(id = R.string.text_error_loading_details),
        modifier = Modifier.align(Alignment.Center),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.displayMedium,
        textAlign = TextAlign.Center
    )
}