package io.musicorum.mobile.ktor.endpoints.musicorum.generator.models

import io.musicorum.mobile.ktor.endpoints.musicorum.generator.Generator
import io.musicorum.mobile.ktor.endpoints.musicorum.generator.IThemeOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BaseBody(
    val user: String,
    val theme: String,
    val language: String,
    @Serializable(with = Generator.ThemeSerializer::class)
    val options: IThemeOptions,
    val story: Boolean = false,
    @SerialName("hide_username")
    val hideUsername: Boolean = false
)
