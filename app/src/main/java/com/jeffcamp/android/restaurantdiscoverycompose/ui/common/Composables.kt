package com.jeffcamp.android.restaurantdiscoverycompose.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.jeffcamp.android.restaurantdiscoverycompose.R
import com.jeffcamp.android.restaurantdiscoverycompose.domain.model.PlaceDetail
import com.jeffcamp.android.restaurantdiscoverycompose.ui.search.DisplayMode

@Composable
fun BoxScope.ToggleViewButton(
    currentDisplayMode: DisplayMode,
    onButtonClick: (displayMode: DisplayMode) -> Unit
) {
    val textResId = if (currentDisplayMode == DisplayMode.MAP) {
        R.string.button_list
    } else {
        R.string.button_map
    }
    val drawableResId = if (currentDisplayMode == DisplayMode.MAP) {
        R.drawable.ic_list
    } else {
        R.drawable.ic_map
    }
    val contentDescResId = if (currentDisplayMode == DisplayMode.MAP) {
        R.string.content_description_list
    } else {
        R.string.content_description_map
    }

    Button(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 24.dp)
            .height(48.dp),
        elevation =  ButtonDefaults.buttonElevation(
            defaultElevation = 5.dp,
            pressedElevation = 2.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        onClick = {
            onButtonClick.invoke(if (currentDisplayMode == DisplayMode.LIST) DisplayMode.MAP else DisplayMode.LIST)
        }
    ) {
        Image(
            painterResource(id = drawableResId),
            contentDescription = stringResource(contentDescResId)
        )
        val middle = if (currentDisplayMode == DisplayMode.MAP) {
            15.75.dp
        } else {
            14.25.dp
        }
        Spacer(modifier = Modifier.width(middle))
        Text(
            text = stringResource(textResId),
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun BasicRestaurantInfo(placeDetail: PlaceDetail, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = placeDetail.name,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(id = R.drawable.ic_star),
                contentDescription = stringResource(R.string.content_description_star)
            )

            Text(
                text = placeDetail.rating.toString(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = "\u2022",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = "(${placeDetail.ratingsTotal})",
                color = MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row {
            placeDetail.isOpenNow?.let { isOpen ->
                Text(
                    text = if (isOpen) stringResource(R.string.text_open) else stringResource(R.string.text_closed),
                    color = MaterialTheme.colorScheme.onTertiary,
                    style = MaterialTheme.typography.labelMedium
                )

                placeDetail.priceLevel?.let {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "\u2022",
                        color = MaterialTheme.colorScheme.onTertiary,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
            }

            placeDetail.priceLevel?.let { priceLevel ->
                val stringBuilder = StringBuilder()
                repeat(priceLevel) {
                    stringBuilder.append("$")
                }
                Text(
                    text = stringBuilder.toString(),
                    color = MaterialTheme.colorScheme.onTertiary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable fun RestaurantInfoRow(
    text: String,
    drawableResId: Int,
    contentDescription: String,
    uriString: String,
    modifier: Modifier = Modifier,
    showTopLine: Boolean = true,
    showBottomLine: Boolean = true
) {
    if (showTopLine) Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painterResource(id = drawableResId),
            contentDescription = contentDescription
        )
        Spacer(modifier = Modifier.width(16.dp))
        val uriHandler = LocalUriHandler.current
        ClickableText(
            text = AnnotatedString.Builder(text).toAnnotatedString(),
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground)
        ) {
            uriHandler.openUri(uriString)
        }
    }

    if (showBottomLine) Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
}