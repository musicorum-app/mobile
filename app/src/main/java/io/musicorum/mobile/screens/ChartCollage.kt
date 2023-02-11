package io.musicorum.mobile.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.utils.downloadFile
import io.musicorum.mobile.utils.shareFile
import io.musicorum.mobile.viewmodels.ChartCollageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartCollage(model: ChartCollageViewModel = viewModel()) {
    val scrollbarBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val themeDropdown = remember { mutableStateOf(false) }
    val typeDropdown = remember { mutableStateOf(false) }
    val periodDropdown = remember { mutableStateOf(false) }
    val user = LocalUser.current?.user
    val generatedImageUrl = model.imageUrl.observeAsState().value
    val ready = model.ready.observeAsState().value!!
    val ctx = LocalContext.current

    val themeOptions = listOf("Grid" to "Grid")
    val typeOptions =
        listOf("Top albums" to "ALBUM", "Top artists" to "ARTIST", "Top tracks" to "ALBUM")
    val periodOptions =
        listOf(
            "Last 7 days" to "7DAY",
            "Last 30 days" to "1MONTH",
            "Last 3 months" to "3MONTH",
            "Last 6 months" to "6MONTH",
            "Last year" to "12MONTH",
            "Overall" to "OVERALL"
        )

    val selectedTheme = remember { mutableStateOf("Grid" to "grid") }
    val selectedType = remember { mutableStateOf("Top artists" to "ARTIST") }
    val selectedPeriod = remember { mutableStateOf("Last week" to "7DAY") }
    val showNames = remember { mutableStateOf(true) }

    val rowCount = remember { mutableStateOf("6") }
    val colCount = remember { mutableStateOf("6") }

    val rowError = rowCount.value.toIntOrNull() == null
            || rowCount.value.toInt() < 3
            || rowCount.value.toInt() > 10
    val colError = colCount.value.toIntOrNull() == null
            || colCount.value.toInt() < 3
            || colCount.value.toInt() > 10

    val isGenerating = remember { mutableStateOf(false) }

    Scaffold(topBar = {
        MusicorumTopBar(
            text = "Generate Collage",
            scrollBehavior = scrollbarBehavior,
            fadeable = true
        ) {

        }
    }) { paddingValues ->
        if (user == null) return@Scaffold CenteredLoadingSpinner()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DropdownMenu(
                openState = themeDropdown,
                valueState = selectedTheme,
                options = themeOptions,
                fieldLabel = "Theme"
            )
            DropdownMenu(
                openState = typeDropdown,
                valueState = selectedType,
                options = typeOptions,
                fieldLabel = "Type"
            )
            DropdownMenu(
                openState = periodDropdown,
                valueState = selectedPeriod,
                options = periodOptions,
                fieldLabel = "Period"
            )
            /* ROWS AND COLS */
            Row {
                OutlinedTextField(
                    value = rowCount.value,
                    onValueChange = { rowCount.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(text = "Rows") },
                    modifier = Modifier.weight(.5f, true),
                    isError = rowError,
                    singleLine = true,
                    supportingText = { Text("From 3 to 10") }
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = colCount.value,
                    onValueChange = { colCount.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(text = "Columns") },
                    modifier = Modifier.weight(.5f, true),
                    isError = colError,
                    singleLine = true,
                    supportingText = { Text("From 3 to 10") }
                )
            }
            /* SHOW NAMES */
            Row(verticalAlignment = Alignment.CenterVertically) {
                val switchColors = SwitchDefaults.colors(
                    checkedTrackColor = MostlyRed,
                    uncheckedThumbColor = ContentSecondary,
                    uncheckedTrackColor = LighterGray,
                    uncheckedBorderColor = EvenLighterGray
                )
                Switch(
                    checked = showNames.value,
                    onCheckedChange = { showNames.value = !showNames.value },
                    colors = switchColors
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Show names")
            }

            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = MostlyRed
            )
            Button(
                onClick = {
                    isGenerating.value = true
                    model.generate(
                        user.name,
                        rowCount.value.toInt(),
                        colCount.value.toInt(),
                        selectedType.value.second,
                        selectedPeriod.value.second,
                        showNames.value
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGenerating.value,
                colors = buttonColors
            ) {
                if (isGenerating.value) {
                    CircularProgressIndicator(modifier = Modifier.size(25.dp), strokeWidth = 3.dp)
                } else {
                    Text(text = "Generate", textAlign = TextAlign.Center)
                }
            }

            /* DISPLAY IMAGE */
            AnimatedVisibility(visible = ready) {
                isGenerating.value = false
                val shareText =
                    "Check out my ${colCount.value}x${rowCount.value} collage, made with the Musicorum mobile app"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    AsyncImage(
                        model = defaultImageRequestBuilder(url = generatedImageUrl),
                        contentDescription = null,
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val uri = Uri.parse(generatedImageUrl)
                        val outlinedButtonColors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                        OutlinedButton(
                            onClick = { shareFile(ctx, uri, shareText) },
                            modifier = Modifier.weight(.5f, true),
                            colors = outlinedButtonColors
                        ) {
                            Icon(Icons.Rounded.Share, null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Share")
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Button(onClick = {
                            downloadFile(ctx, uri = uri)
                        }, modifier = Modifier.weight(.5f, true)) {
                            Icon(Icons.Rounded.Download, null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Save")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(
    openState: MutableState<Boolean>,
    valueState: MutableState<Pair<String, String>>,
    options: List<Pair<String, String>>,
    fieldLabel: String
) {
    ExposedDropdownMenuBox(
        expanded = openState.value,
        onExpandedChange = { openState.value = !openState.value },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = valueState.value.first,
            onValueChange = {},
            label = { Text(text = fieldLabel) },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .focusRequester(FocusRequester())
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openState.value) }
        )
        val menuColors = MenuDefaults.itemColors()
        ExposedDropdownMenu(
            expanded = openState.value,
            modifier = Modifier.background(LighterGray),
            onDismissRequest = { openState.value = false }) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it.first) },
                    onClick = { valueState.value = it; openState.value = false },
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.background(LighterGray)
                )
            }
        }
    }
}
