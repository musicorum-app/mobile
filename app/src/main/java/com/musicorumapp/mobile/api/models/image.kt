package com.musicorumapp.mobile.api.models

import com.google.gson.annotations.SerializedName

data class ImageResourceSerializable(
    val size: String,

    @SerializedName("#text")
    val url: String
)

data class LastfmImages(
    val images: List<String>
) {
    val bestImage: String
        get() = images.last()

    companion object {
        fun fromSerializable(images: List<ImageResourceSerializable>): LastfmImages {
            return LastfmImages(
                images = images.map { it.url }
            )
        }
    }
}