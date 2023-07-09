package io.musicorum.mobile.models

enum class ResourceEntity(entity: String) {
    Artist("ARTIST"),
    Album("ALBUM"),
    Track("TRACK");

    val entityName = entity
}