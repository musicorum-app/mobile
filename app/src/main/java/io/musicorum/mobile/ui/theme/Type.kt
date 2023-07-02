package io.musicorum.mobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
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

val BodyLarge = TextStyle(
    fontFamily = Author,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
)

val Subtitle1 = TextStyle(
    fontFamily = Author,
    fontWeight = FontWeight(400),
    fontSize = 12.sp,
    color = Color(255, 255, 255, 140),
)

val LabelMedium2 = TextStyle(
    fontFamily = Author,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    color = Color(255, 255, 255, 140)
)

private const val FEATURES = "'pnum' on, 'lnum' on, 'salt' on"


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        fontFeatureSettings = FEATURES
    ),
    bodyMedium = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        fontFeatureSettings = FEATURES
    ),
    titleLarge = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        fontFeatureSettings = FEATURES
    ),
    titleMedium = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        fontFeatureSettings = FEATURES
    ),
    titleSmall = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        fontFeatureSettings = FEATURES
    ),
    labelMedium = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        fontFeatureSettings = FEATURES
    ),
    headlineLarge = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        fontFeatureSettings = FEATURES
    ),
    headlineMedium = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        fontFeatureSettings = FEATURES,
    ),
    headlineSmall = TextStyle(
        fontFamily = Author,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        fontFeatureSettings = FEATURES
    ),
    displayLarge = TextStyle(
        fontFamily = Author,
        fontSize = 57.sp,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = FEATURES
    ),
    displayMedium = TextStyle(
        fontFamily = Author,
        fontSize = 45.sp,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = FEATURES
    ),
    displaySmall = TextStyle(
        fontFamily = Author,
        fontSize = 36.sp,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = FEATURES
    ),
)