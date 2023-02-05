package com.jeffcamp.android.restaurantdiscoverycompose.ext

import android.Manifest
import androidx.activity.result.ActivityResultLauncher

fun ActivityResultLauncher<Array<String>>.requestLocationPermission() {
    launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
}