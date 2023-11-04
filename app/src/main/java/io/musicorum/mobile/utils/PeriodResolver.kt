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

    fun resolve(period: String?): FetchPeriod {
        return when (period?.uppercase(Locale.ROOT)) {
            "7DAY" -> FetchPeriod.WEEK
            "1MONTH" -> FetchPeriod.MONTH
            "3MONTH" -> FetchPeriod.TRIMESTER
            "6MONTH" -> FetchPeriod.SEMESTER
            "12MONTH" -> FetchPeriod.YEAR
            "OVERALL" -> FetchPeriod.OVERALL
            else -> FetchPeriod.WEEK
        }
    }
}