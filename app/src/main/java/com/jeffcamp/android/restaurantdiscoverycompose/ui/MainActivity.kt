package com.jeffcamp.android.restaurantdiscoverycompose.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.*
import com.jeffcamp.android.restaurantdiscoverycompose.ext.isLocationPermissionGranted
import com.jeffcamp.android.restaurantdiscoverycompose.ext.requestLocationPermission
import com.jeffcamp.android.restaurantdiscoverycompose.network.datasource.NetworkDataSource
import com.jeffcamp.android.restaurantdiscoverycompose.repository.PlaceDetailsRepository
import com.jeffcamp.android.restaurantdiscoverycompose.repository.SearchRepository
import com.jeffcamp.android.restaurantdiscoverycompose.ui.details.DetailsScreen
import com.jeffcamp.android.restaurantdiscoverycompose.ui.details.DetailsViewModel
import com.jeffcamp.android.restaurantdiscoverycompose.ui.search.SearchScreen
import com.jeffcamp.android.restaurantdiscoverycompose.ui.search.SearchViewModel
import com.jeffcamp.android.restaurantdiscoverycompose.ui.theme.RestaurantDiscoveryComposeTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.containsValue(true)) {
            onLocationPermissionGranted()
        } else {
            searchViewModel.onLocationPermissionDenied()
        }
    }

    private val networkDataSource = NetworkDataSource()

    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModel.provideFactory(
            locationClient = LocationServices.getFusedLocationProviderClient(this),
            searchRepository = SearchRepository(networkDataSource),
            owner = this
        )
    }

    private val detailsViewModel: DetailsViewModel by viewModels {
        DetailsViewModel.provideFactory(
            placeDetailsRepository = PlaceDetailsRepository(networkDataSource),
            owner = this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLocationPermissionGranted) {
            onLocationPermissionGranted()
        } else {
            requestPermissionLauncher.requestLocationPermission()
        }

        setContent {
            RestaurantDiscoveryComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationController(searchViewModel, detailsViewModel)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun onLocationPermissionGranted() {
        searchViewModel.onLocationPermissionGranted()
    }
}

private const val NAV_ARG_PLACE_ID = "placeId"
private const val NAV_ROUTE_SEARCH = "search"
private val String.navRouteDetail
    get() = "detail/$this"

@Composable
fun NavigationController(searchViewModel: SearchViewModel, detailsViewModel: DetailsViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NAV_ROUTE_SEARCH) {
        composable(NAV_ROUTE_SEARCH) {
            SearchScreen(searchViewModel) { placeId ->
                navController.navigate(placeId.navRouteDetail)
            }
        }
        composable(
            route = "{${NAV_ARG_PLACE_ID}}".navRouteDetail,
            arguments = listOf(navArgument(NAV_ARG_PLACE_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            DetailsScreen(detailsViewModel, backStackEntry.arguments?.getString(NAV_ARG_PLACE_ID).orEmpty())
        }
    }
}
