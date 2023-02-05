package com.jeffcamp.android.restaurantdiscoverycompose.app

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class TheApplication : Application() {

    var googleApiKey: String? = null

    override fun onCreate() {
        super.onCreate()

        application = this

        val appInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        googleApiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")
    }

    companion object {
        var application: TheApplication? = null
    }
}