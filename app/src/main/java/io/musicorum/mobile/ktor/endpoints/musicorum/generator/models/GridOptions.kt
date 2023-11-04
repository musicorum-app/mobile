package io.musicorum.mobile.ktor.endpoints.musicorum.generator.models

import io.musicorum.mobile.ktor.endpoints.musicorum.generator.IGridOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GridOptions(
    @SerialName("show_playcount")
    override val showPlayCount: Boolean,
    override val rows: Int?,
    override val columns: Int?,
    @SerialName("show_names")
    override val showNames: Boolean,
    override val period: String,
    override val entity: String,
    override val style: String
) : IGridOptions
