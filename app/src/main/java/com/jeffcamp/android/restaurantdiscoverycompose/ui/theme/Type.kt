package com.jeffcamp.android.restaurantdiscoverycompose.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jeffcamp.android.restaurantdiscoverycompose.R

private val fontFamilyManropeMedium = FontFamily(Font(R.font.manrope_medium))
private val fontFamilyManropeBold = FontFamily(Font(R.font.manrope_bold))

// TODO: define full set of typography
val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = fontFamilyManropeBold,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = fontFamilyManropeMedium,
        fontSize = 22.sp,
        lineHeight = 30.05.sp,
        letterSpacing = (-0.1).sp
    ),
    titleMedium = TextStyle(
        fontFamily = fontFamilyManropeBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = fontFamilyManropeMedium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = fontFamilyManropeMedium,
        fontSize = 13.sp,
        lineHeight = 19.5.sp
    )
)