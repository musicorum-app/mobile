package com.musicorumapp.mobile.api.models

import com.squareup.moshi.Json


data class ImageResourceSerializable(
    val size: String,

    @field:Json(name = "#text")
    val url: String
)

data class LastfmImages(
    val images: List<String>,
    val type: LastfmEntity,
) {
    val bestImage: String?
        get() {
            if (images.isEmpty()) return null
            val last = images.last()
            return if (last == "") null else last
        }

    companion object {
        fun fromSerializable(images: List<ImageResourceSerializable>, type: LastfmEntity): LastfmImages {
            return LastfmImages(
                images = images.map { it.url },
                type = type
            )
        }

        fun fromEmpty(type: LastfmEntity): LastfmImages = LastfmImages(emptyList(), type)
    }
}