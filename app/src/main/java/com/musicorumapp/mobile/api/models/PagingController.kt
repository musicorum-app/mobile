package com.musicorumapp.mobile.api.models

class PagingController<T: PageableItem> (
    val perPage: Int = 20,
    var totalResults: Int = 0,
    private val pages: MutableMap<Int, List<T>> = mutableMapOf(),
    val requester: suspend (page: Int) -> List<T>
) {
    fun addPageContent(page: Int, items: List<T>): PagingController<T> {
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

    suspend fun doRequest(page: Int): List<T> {
        val results = requester(page)
        addPageContent(page, results)
        return results
    }

    override fun toString(): String {
        var type = "Unknown"
        if (getAllItems().isNotEmpty()) {
            type = getAllItems().first().javaClass.simpleName
        }
        return "PagingController<$type>(perPage = $perPage, pages = ${pages.size}, items = ${getAllItems().size}, totalResults = ${totalResults})"
    }
}
