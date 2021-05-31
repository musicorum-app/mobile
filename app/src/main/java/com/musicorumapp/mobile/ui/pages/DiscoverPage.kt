package com.musicorumapp.mobile.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.states.models.AuthenticationViewModel
import com.musicorumapp.mobile.states.models.DiscoverPageViewModel
import com.musicorumapp.mobile.ui.components.Title
import com.musicorumapp.mobile.ui.theme.PaddingSpacing

@Composable
fun DiscoverPage(
    authenticationViewModel: AuthenticationViewModel?,
    discoverPageViewModel: DiscoverPageViewModel?
) {

    val searchValue = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = PaddingSpacing.HorizontalMainPadding)
            .padding(top = 6.dp)
    ) {
        Title(text = stringResource(id = R.string.bottom_navigation_item_discover))

        Spacer(modifier = Modifier.height(6.dp))
        TextField(
            value = searchValue.value,
            onValueChange = { searchValue.value = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFF484848),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(6.dp),
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            singleLine = true,
            keyboardActions = KeyboardActions {
                Log.i(Constants.LOG_TAG, searchValue.value)
                discoverPageViewModel?.search(searchValue.value)
            }
        )
    }
}