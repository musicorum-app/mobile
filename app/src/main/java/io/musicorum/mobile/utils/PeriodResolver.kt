package io.musicorum.mobile.utils

import io.musicorum.mobile.models.FetchPeriod
import java.util.Locale

object PeriodResolver {
    fun resolve(period: FetchPeriod): String {
        return when (period) {
            FetchPeriod.MONTH -> "last 30 days"
            FetchPeriod.WEEK -> "last 7 days"
            FetchPeriod.TRIMESTER -> "last 3 months"
            FetchPeriod.SEMESTER -> "last 6 months"
            FetchPeriod.YEAR -> "last 12 months"
            FetchPeriod.OVERALL -> "overall"
        }
    }

    fun resolve(period: String): Pair<String, String> {
        return when (period.uppercase(Locale.ROOT)) {
            "7DAY" -> "Last week" to "7DAY"
            "1MONTH" -> "Last month" to "1MONTH"
            "3MONTH" -> "Last 3 months" to "3MONTH"
            "6MONTH" -> "Last 6 months" to "6MONTH"
            "12MONTH" -> "Last year" to "12MONTH"
            "OVERALL" -> "Overall" to "OVERALL"
            else -> "Last week" to "7DAY"
        }
    }
}