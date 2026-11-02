package io.musicorum.mobile.repositories

import java.util.Locale
import java.util.concurrent.TimeUnit

internal object DetailCachePolicy {
    const val MAX_ENTRIES = 30
    val DETAILS_FRESHNESS_MILLIS: Long = TimeUnit.MINUTES.toMillis(10)
    val ARTWORK_FRESHNESS_MILLIS: Long = TimeUnit.DAYS.toMillis(7)
    val RELATED_FRESHNESS_MILLIS: Long = TimeUnit.HOURS.toMillis(1)
}

internal class BoundedLruCache<K, V>(maxEntries: Int) {
    private val entries = object : LinkedHashMap<K, V>(
        maxEntries,
        CACHE_LOAD_FACTOR,
        true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean {
            return size > maxEntries
        }
    }

    @Synchronized
    operator fun get(key: K): V? = entries[key]

    @Synchronized
    fun put(key: K, value: V) {
        entries[key] = value
    }

    private companion object {
        const val CACHE_LOAD_FACTOR = 0.75f
    }
}

internal fun String.normalizedCacheValue(): String {
    return trim().lowercase(Locale.ROOT)
}

internal fun isCacheValueFresh(
    now: Long,
    updatedAt: Long,
    freshnessMillis: Long
): Boolean {
    return now - updatedAt < freshnessMillis
}
