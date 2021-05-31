package com.musicorumapp.mobile.api.models

import com.google.gson.annotations.SerializedName
import com.musicorumapp.mobile.states.models.SearchResults
import kotlin.reflect.typeOf

class SearchController<T: SearchableItem> (
    val perPage: Int = 20,
    private val pages: MutableMap<Int, List<T>> = mutableMapOf(),
    val searchMethod: suspend (page: Int) -> List<T>
) {
    fun addPageContent(page: Int, items: List<T>): SearchController<T> {
        pages[page] = items
        return this
    }

    fun getPageContent(page: Int): List<T>? {
        return pages[page]
    }

    fun getAllItems(): List<T> {
        val items: MutableList<T> = mutableListOf()

        pages.forEach { items.addAll(it.value) }

        return items
    }

    suspend fun doSearch(page: Int): List<T> {
        val results = searchMethod(page)
        addPageContent(page, results)
        return results
    }

    override fun toString(): String {
        var type = "Unknown"
        if (getAllItems().isNotEmpty()) {
            type = getAllItems().first().javaClass.simpleName
        }
        return "SearchController<$type>(perPage = $perPage, pages = ${pages.size}, items = ${getAllItems().size})"
    }
}
