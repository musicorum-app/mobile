package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.musicorum.Generator
import kotlinx.coroutines.launch

class ChartCollageViewModel : ViewModel() {
    val imageUrl: MutableLiveData<String> = MutableLiveData()
    val ready = MutableLiveData(false)

    fun generate(
        username: String,
        rowCount: Int,
        colCount: Int,
        entity: String,
        period: String,
        showNames: Boolean
    ) {
        ready.value = false
        viewModelScope.launch {
            val url = Generator.generateGrid(username, rowCount, colCount, entity, period, showNames)
            imageUrl.value = url
            ready.value = true
        }
    }
}