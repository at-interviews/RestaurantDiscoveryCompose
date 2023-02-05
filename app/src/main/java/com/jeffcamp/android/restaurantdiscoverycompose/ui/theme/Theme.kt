package com.jeffcamp.android.restaurantdiscoverycompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// TODO: define full set of colors
private val LightColorScheme = lightColorScheme(
    primary = White,
    secondary = Green700,
    tertiary = Grey200,
    background = Grey200,
    surface = White,
    outline = Grey200,

    onPrimary = Green900,
    onSecondary = White,
    onTertiary = Grey600,
    onBackground = Grey800,
    onSurface = Grey800
)

// TODO: implement for dark mode
//private val DarkColorScheme = darkColorScheme(
//    primary = White,
//    secondary = Green700,
//    tertiary = Grey200,
//    background = Grey200,
//    surface = White,
//    outline = Grey200,
//
//    onPrimary = Green900,
//    onSecondary = White,
//    onTertiary = Grey600,
//    onBackground = Grey800,
//    onSurface = Grey800,
//)

@Composable
fun RestaurantDiscoveryComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    if (!LocalView.current.isInEditMode) {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            // Update all of the system bar colors to be transparent, and use
            // dark icons if we're in light theme
            systemUiController.setSystemBarsColor(
                color = colorScheme.primary,
                darkIcons = !darkTheme
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}