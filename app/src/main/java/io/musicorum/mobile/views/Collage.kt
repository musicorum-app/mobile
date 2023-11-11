package io.musicorum.mobile.views

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.MusicorumTheme
import io.musicorum.mobile.models.ResourceEntity
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.utils.PeriodResolver
import io.musicorum.mobile.viewmodels.CollageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Collage(viewModel: CollageViewModel = viewModel(), args: Bundle) {
    val scrollbarBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val themeDropdown = remember { mutableStateOf(false) }
    val typeDropdown = remember { mutableStateOf(false) }
    val periodDropdown = remember { mutableStateOf(false) }
    val generatedImageUrl = viewModel.imageUrl.observeAsState().value
    val ready = viewModel.ready.observeAsState().value!!
    val isGenerating = viewModel.isGenerating.observeAsState().value!!
    val nav = LocalNavigation.current

    val themeOptions = listOf("Grid" to MusicorumTheme.GRID, "Duotone" to MusicorumTheme.DUOTONE)
    val entityOptions =
        listOf(
            "Top albums" to ResourceEntity.Album,
            "Top artists" to ResourceEntity.Artist,
            "Top tracks" to ResourceEntity.Track
        )
    val periodOptions =
        listOf(
            "Last 7 days" to FetchPeriod.WEEK,
            "Last 30 days" to FetchPeriod.MONTH,
            "Last 3 months" to FetchPeriod.TRIMESTER,
            "Last 6 months" to FetchPeriod.SEMESTER,
            "Last year" to FetchPeriod.YEAR,
            "Overall" to FetchPeriod.OVERALL
        )

    val selectedTheme by viewModel.selectedTheme.observeAsState(MusicorumTheme.GRID)
    args.getString("period")?.let {
        viewModel.selectedPeriod.value = PeriodResolver.resolve(it)
    }
    val showNames = remember { mutableStateOf(true) }

    val rowCount by viewModel.gridRowCount.observeAsState(6)
    val colCount by viewModel.gridColCount.observeAsState(6)

    val rowError = rowCount !in 3..10
    val colError = colCount !in 3..10

    val generateEnabled = if (isGenerating) {
        false
    } else if (rowError) false else !colError
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(topBar = {
        MediumTopAppBar(
            title = { Text("Generate Collage") },
            scrollBehavior = scrollbarBehavior,
            navigationIcon = {
                IconButton(onClick = { nav?.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .background(KindaBlack)
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DropdownMenu(
                openState = themeDropdown,
                options = themeOptions,
                fieldLabel = stringResource(R.string.theme)
            ) {
                viewModel.selectedTheme.value = it as MusicorumTheme
            }
            DropdownMenu(
                openState = typeDropdown,
                options = entityOptions,
                fieldLabel = stringResource(R.string.type)
            ) {
                viewModel.selectedEntity.value = it as ResourceEntity
            }
            DropdownMenu(
                openState = periodDropdown,
                options = periodOptions,
                fieldLabel = stringResource(R.string.period)
            ) {
                viewModel.selectedPeriod.value = it as FetchPeriod
            }

            /* ROWS AND COLS */
            AnimatedVisibility(visible = selectedTheme == MusicorumTheme.GRID) {
                Column {
                    Row {
                        OutlinedTextField(
                            value = rowCount.toString(),
                            onValueChange = {
                                viewModel.gridRowCount.value = it.toIntOrNull() ?: 6
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(text = stringResource(R.string.rows)) },
                            modifier = Modifier.weight(.5f, true),
                            isError = rowError,
                            singleLine = true,
                            supportingText = { Text(stringResource(R.string.from_3_to_10)) }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = colCount.toString(),
                            onValueChange = {
                                viewModel.gridColCount.value = it.toIntOrNull() ?: 6
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(text = stringResource(R.string.columns)) },
                            modifier = Modifier.weight(.5f, true),
                            isError = colError,
                            singleLine = true,
                            supportingText = { Text(stringResource(R.string.from_3_to_10)) }
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
                        Text(text = stringResource(R.string.show_names))
                    }
                }
            }

            AnimatedVisibility(visible = selectedTheme == MusicorumTheme.DUOTONE) {
                val duotoneThemeState = remember {
                    mutableStateOf(false)
                }
                val duotoneThemes = listOf(
                    "Purplish" to "Purplish",
                    "Natural" to "Natural",
                    "Divergent" to "Divergent",
                    "Bright Sun" to "Bright Sun",
                    "Yellish" to "Yellish",
                    "Horror" to "Horror",
                    "Sea" to "Sea"
                )

                val themeColors = listOf(
                    Color(0xff16006f) to Color(0xfff7396f),
                    Color(0xff1a2a56) to Color(0xff00d574),
                    Color(0xffa21685) to Color(0xff63acbb),
                    Color(0xffea1264) to Color(0xFFD7FD31),
                    Color(0xFF141209) to Color(0xFFFFEA00),
                    Color(0xFF000000) to Color(0xFFDC2C2C),
                    Color(0xFF0239D8) to Color(0xFF68EBC1),
                )
                Column {
                    DropdownMenu(
                        openState = duotoneThemeState,
                        options = duotoneThemes,
                        fieldLabel = stringResource(R.string.palette),
                        leadingIcon = {
                            val colorPair = themeColors[it]
                            Canvas(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                            ) {
                                drawArc(
                                    color = colorPair.first,
                                    startAngle = 150f,
                                    sweepAngle = 180f,
                                    useCenter = false,
                                    style = Fill
                                )
                                drawArc(
                                    color = colorPair.second,
                                    startAngle = 330f,
                                    sweepAngle = 180f,
                                    useCenter = false,
                                    style = Fill
                                )
                            }
                        }
                    ) {
                        viewModel.duotonePalette.value = it as String
                    }
                    val storyModeState by viewModel.storyMode.observeAsState(true)
                    val hideUsername by viewModel.hideUsername.observeAsState(false)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = storyModeState, onCheckedChange = {
                            viewModel.storyMode.value = it
                        })
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(stringResource(R.string.story_format))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = hideUsername, onCheckedChange = {
                            viewModel.hideUsername.value = it
                        })
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(stringResource(R.string.hide_username))
                    }
                }
            }

            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = MostlyRed
            )
            Button(
                onClick = {
                    keyboardController?.hide()
                    if (selectedTheme == MusicorumTheme.GRID) {
                        viewModel.generateGrid(showNames.value)
                    } else {
                        viewModel.generateDuotone()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = generateEnabled,
                colors = buttonColors
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(25.dp), strokeWidth = 3.dp)
                } else {
                    Text(text = stringResource(R.string.generate), textAlign = TextAlign.Center)
                }
            }

            /* DISPLAY IMAGE */
            AnimatedVisibility(visible = ready && viewModel.imageUrl.value != null) {
                val ctx = LocalContext.current
                val imageModel = ImageRequest.Builder(ctx)
                    .crossfade(true)
                    .data(generatedImageUrl)
                    .build()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = imageModel,
                        contentDescription = "collage image",
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)),
                        loading = {
                            CircularProgressIndicator()
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val outlinedButtonColors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                        OutlinedButton(
                            onClick = { viewModel.shareFile() },
                            modifier = Modifier.weight(.5f, true),
                            colors = outlinedButtonColors
                        ) {
                            Icon(Icons.Rounded.Share, null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = stringResource(R.string.share))
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Button(
                            onClick = { viewModel.downloadFile() },
                            modifier = Modifier.weight(.5f, true)
                        ) {
                            Icon(Icons.Rounded.Download, null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = stringResource(R.string.save))
                        }
                    }
                }
            }

            AnimatedVisibility(visible = ready && viewModel.imageUrl.value == null) {
                val errorMessage by viewModel.errorMessage.observeAsState("")
                Text(
                    text = stringResource(
                        R.string.generator_error,
                        errorMessage
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(
    openState: MutableState<Boolean>,
    options: List<Pair<String, Any>>,
    fieldLabel: String,
    leadingIcon: (@Composable (index: Int) -> Unit)? = null,
    onChange: (value: Any) -> Unit
) {
    val choice = rememberSaveable {
        mutableStateOf(options.first().first)
    }
    ExposedDropdownMenuBox(
        expanded = openState.value,
        onExpandedChange = { openState.value = !openState.value },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = choice.value,
            onValueChange = {},
            label = { Text(text = fieldLabel) },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .focusRequester(FocusRequester())
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openState.value) }
        )
        ExposedDropdownMenu(
            expanded = openState.value,
            modifier = Modifier.background(LighterGray),
            onDismissRequest = { openState.value = false }) {
            options.forEachIndexed { i, v ->
                DropdownMenuItem(
                    text = { Text(v.first) },
                    onClick = {
                        onChange(v.second)
                        choice.value = v.first
                        openState.value = false
                    },
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.background(LighterGray),
                    leadingIcon = if (leadingIcon == null) {
                        null
                    } else {
                        {
                            leadingIcon(i)
                        }
                    }
                )
            }
        }
    }
}
