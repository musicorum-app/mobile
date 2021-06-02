package com.musicorumapp.mobile.states.models

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicorumapp.mobile.utils.PredominantColorState
import kotlinx.coroutines.launch

class HomePageViewModel(
    private val authenticationViewModel: AuthenticationViewModel
) : ViewModel() {

    private val _colorFetched = MutableLiveData(false)

    private val _predominantColor = MutableLiveData<Color?>(null)

    val colorFetched: LiveData<Boolean> = _colorFetched

    val predominantColor: LiveData<Color?> = _predominantColor

    fun fetchColors(predominantColorState: PredominantColorState, url: String) {
        viewModelScope.launch {
            predominantColorState.resolveColorsFromURL(url) // TODO = better image placeholder
            _colorFetched.value = true
        }
    }
}