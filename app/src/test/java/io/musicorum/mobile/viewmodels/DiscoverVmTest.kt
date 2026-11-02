package io.musicorum.mobile.viewmodels

import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.serialization.musicorum.Images
import io.musicorum.mobile.serialization.musicorum.Resources
import io.musicorum.mobile.serialization.musicorum.TrackResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DiscoverVmTest {
    @Test
    fun userResultsStartEmpty() {
        assertEquals(emptyList<User>(), DiscoverVm().userResult.value)
    }

    @Test
    fun imageLookupHandlesMissingEnrichmentResult() {
        val imageUrl = "https://example.com/image.jpg"
        val resourceHash = "resource"
        val responses = listOf(
            TrackResponse(
                resources = listOf(
                    Resources(
                        images = listOf(Images(imageUrl, "EXTRA_LARGE")),
                        hash = resourceHash
                    )
                ),
                preferredResource = resourceHash
            )
        )

        assertEquals(imageUrl, responses.bestImageUrlAt(0))
        assertNull(responses.bestImageUrlAt(1))
    }
}
