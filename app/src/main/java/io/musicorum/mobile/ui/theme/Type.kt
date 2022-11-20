package io.musicorum.mobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.musicorum.mobile.R

val Author = FontFamily(
    Font(R.font.author_regular, FontWeight.Normal),
    Font(R.font.author_bold, FontWeight.Bold),
    Font(R.font.author_light, FontWeight.Light),
    Font(R.font.author_medium, FontWeight.Medium),
    Font(R.font.author_semibold, FontWeight.SemiBold),
    Font(R.font.author_extralight, FontWeight.ExtraLight),
)

val Heading2 = TextStyle(
    fontFamily = Author,
    fontWeight = FontWeight(600),
    fontSize = 28.sp,
)

val Heading4 = TextStyle(
    fontFamily = Author,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
)

val BodySmall = TextStyle(
    fontFamily = Author,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val BodyLarge = TextStyle(
    fontFamily = Author,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val Subtitle1 = TextStyle(
    fontFamily = Author,
    fontWeight = FontWeight(400),
    fontSize = 12.sp,
    color = Color(255, 255, 255, 140),
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

private const val features = "'pnum' on, 'lnum' on, 'salt' on"


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        fontFeatureSettings = features
    ),
    bodyMedium = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        fontFeatureSettings = features
    ),
    titleLarge = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        fontFeatureSettings = features
    ),
    titleMedium = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        fontFeatureSettings = features
    ),
    titleSmall = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        fontFeatureSettings = features
    ),
    labelMedium = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        fontFeatureSettings = features
    ),
    headlineLarge = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        fontFeatureSettings = features
    ),
    headlineMedium = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        fontFeatureSettings = features,
    ),
    headlineSmall = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        fontFeatureSettings = features
    ),
    displayLarge = TextStyle(
        fontFamily = Author,
        fontSize = 57.sp,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = features
    ),
    displayMedium = TextStyle(
        fontFamily = Author,
        fontSize = 45.sp,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = features
    ),
    displaySmall = TextStyle(
        fontFamily = Author,
        fontSize = 36.sp,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = features
    ),
)