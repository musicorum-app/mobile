package io.musicorum.mobile.ktor.endpoints.musicorum.generator.models

import io.musicorum.mobile.ktor.endpoints.musicorum.generator.IDuotoneOptions
import kotlinx.serialization.Serializable

@Serializable
internal data class DuotoneOptions(
    override val palette: String,
    override val entity: String,
    override val period: String,
) : IDuotoneOptions
