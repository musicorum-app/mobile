package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.musicorum.Generator
import io.sentry.Sentry
import io.sentry.SpanStatus
import kotlinx.coroutines.launch

class ChartCollageViewModel : ViewModel() {
    val imageUrl: MutableLiveData<String> = MutableLiveData()
    val ready = MutableLiveData(false)
    val isGenerating = MutableLiveData(false)

    fun generate(
        username: String,
        rowCount: Int,
        colCount: Int,
        entity: String,
        period: String,
        showNames: Boolean
    ) {
        val transaction = Sentry.startTransaction("generate", "task")
        transaction.setTag("entity", entity)
        transaction.setTag("collageType", "grid")
        ready.value = false
        isGenerating.value = true
        viewModelScope.launch {
            val url =
                Generator.generateGrid(username, rowCount, colCount, entity, period, showNames)
            imageUrl.value = url
            ready.value = true
            isGenerating.value = false
            if (url == null) {
                transaction.status = SpanStatus.INTERNAL_ERROR
            } else {
                transaction.status = SpanStatus.OK
            }
            transaction.finish()
        }
    }
}