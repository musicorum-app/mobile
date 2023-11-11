package io.musicorum.mobile.ktor.endpoints.musicorum.generator

interface IDuotoneOptions : IThemeOptions {
    val palette: String
    val entity: String
    val period: String
}