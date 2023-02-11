package io.musicorum.mobile.utils

import io.musicorum.mobile.ktor.endpoints.FetchPeriod

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
}