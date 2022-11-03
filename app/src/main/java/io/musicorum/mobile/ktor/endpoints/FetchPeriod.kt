package io.musicorum.mobile.ktor.endpoints

enum class FetchPeriod(period: String) {
    WEEK("7day"),
    MONTH("1month"),
    TRIMESTER("3month"),
    SEMESTER("6month"),
    YEAR("12month"),
    OVERALL("overall");

    val value = period
}