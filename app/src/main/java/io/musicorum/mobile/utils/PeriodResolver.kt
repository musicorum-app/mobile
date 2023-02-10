package io.musicorum.mobile.utils

object PeriodResolver {
    fun resolve(period: String): String {
        return when (period) {
            "month" -> "last 30 days"
            "week" -> "last 7 days"
            "trimester" -> "last 3 months"
            "semester" -> "last 6 months"
            "year" -> "last 12 months"
            "overall" -> "overall"
            else -> ""
        }
    }
}