package io.musicorum.mobile.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey

object AnalyticsConsent {
    const val DataStoreName = "analyticsConsent"
    val CONSENT_KEY = booleanPreferencesKey("consent")
}