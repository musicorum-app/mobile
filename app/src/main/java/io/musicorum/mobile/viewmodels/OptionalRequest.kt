package io.musicorum.mobile.viewmodels

import kotlinx.coroutines.CancellationException

internal suspend fun <T> optionalRequest(block: suspend () -> T): T? {
    return try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (_: Exception) {
        null
    }
}
