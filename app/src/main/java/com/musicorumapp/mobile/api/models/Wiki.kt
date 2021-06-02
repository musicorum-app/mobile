package com.musicorumapp.mobile.api.models

data class Wiki(
    val published: String?,
    val summary: String,
    val content: String
)
data class WikiResponse(
    val published: String?,
    val summary: String,
    val content: String
) {
    fun toWiki(): Wiki = Wiki(
        published = published,
        summary = summary,
        content = content
    )
}