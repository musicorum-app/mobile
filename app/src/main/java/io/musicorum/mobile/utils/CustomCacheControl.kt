package io.musicorum.mobile.utils

import android.util.Log
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.cache.storage.CachedResponseData
import io.ktor.http.Url
import io.ktor.http.fullPath
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.Set
import kotlin.collections.filter
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toSet

object CustomCacheControl : CacheStorage {
    private val caches: MutableMap<Url, CachedResponseData> = mutableMapOf()

    override suspend fun find(url: Url, varyKeys: Map<String, String>): CachedResponseData? {
        Log.d("cache-control", "attempting to get cache for ${url.fullPath}")
        return null
    }

    override suspend fun findAll(url: Url): Set<CachedResponseData> {
        Log.d("cache-control", "attempting to find all caches for ${url.fullPath}")
        Log.d("cache", caches.values.size.toString())
        return caches.values.filter { it.url == url }.toSet()
    }

    override suspend fun store(url: Url, data: CachedResponseData) {
        Log.d("cache-control", "storing data for ${url.fullPath}")
        caches[url] = data
    }
}