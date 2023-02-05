package com.jeffcamp.android.restaurantdiscoverycompose.ext

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

val Context.isLocationPermissionGranted: Boolean
    get() = isCoarseLocationPermissionGranted || isFineLocationPermissionGranted

val Context.isCoarseLocationPermissionGranted: Boolean
    get() {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

val Context.isFineLocationPermissionGranted: Boolean
    get() {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

fun Context.getVectorBitmapDescriptor(vectorResId: Int): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(this, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    drawable.draw(Canvas(bm))
    return BitmapDescriptorFactory.fromBitmap(bm)
}