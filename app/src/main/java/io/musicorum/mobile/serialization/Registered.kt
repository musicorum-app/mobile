package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import java.text.SimpleDateFormat
import java.util.*

@kotlinx.serialization.Serializable
data class Registered(
    @SerialName("unixtime")
    val unixTime: String
) {
    val asParsedDate: String =
        SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(unixTime.toLong() * 1000))
}