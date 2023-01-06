package io.musicorum.mobile.utils

import android.util.Log
import io.ktor.client.plugins.cache.storage.*
import io.ktor.http.*

private object Cache {
    val caches: MutableMap<Url, CachedResponseData> = mutableMapOf()
}

object CustomCacheControl : CacheStorage {

    override suspend fun find(url: Url, varyKeys: Map<String, String>): CachedResponseData? {
        Log.d("cache-control", "attempting to get cache for ${url.fullPath}")
        return null
    }

    override suspend fun findAll(url: Url): Set<CachedResponseData> {
        Log.d("cache-control", "attempting to find all caches for ${url.fullPath}")
        Log.d("cache", Cache.caches.values.size.toString())
        return Cache.caches.values.filter { it.url == url }.toSet()
    }

    override suspend fun store(url: Url, data: CachedResponseData) {
        Log.d("cache-control", "storing data for ${url.fullPath}")
        Cache.caches[url] = data
    }
}