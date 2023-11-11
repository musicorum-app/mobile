package io.musicorum.mobile.ktor.endpoints.musicorum.generator

interface IGridOptions : IThemeOptions {
    val showPlayCount: Boolean
    val rows: Int?
    val columns: Int?
    val showNames: Boolean
    val period: String
    val entity: String
    val style: String
}
