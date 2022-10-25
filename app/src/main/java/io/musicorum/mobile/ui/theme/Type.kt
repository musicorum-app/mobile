package io.musicorum.mobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.musicorum.mobile.R


val Poppins = FontFamily(
    Font(R.font.poppins_black, FontWeight.Black),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_thin, FontWeight.Thin)
)

val Heading2 = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight(600),
    fontSize = 28.sp
)

val Heading4 = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp
)

val Body1 = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight(500),
    fontSize = 18.sp
)

val Body2 = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight(500),
    fontSize = 12.sp
)

val BodySmall = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp
)

val BodyLarge = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp
)

val Subtitle1 = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight(400),
    fontSize = 12.sp
)


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Poppins,
        fontSize = 20.sp
    )
)