package io.musicorum.mobile.views.collage

import android.Manifest
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.R
import io.musicorum.mobile.ktor.endpoints.musicorum.generator.Generator
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.MusicorumTheme
import io.musicorum.mobile.models.ResourceEntity
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.utils.PeriodResolver
import io.sentry.Sentry
import io.sentry.SpanStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL
import java.util.Locale
import javax.inject.Inject

class CollageViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val state = MutableStateFlow(CollageState())
    val ctx = application

    fun updateDuotonePalette(newPalette: String) {
        state.update {
            it.copy(duotonePalette = newPalette)
        }
    }

    fun updatePeriod(period: FetchPeriod) {
        state.update {
            it.copy(selectedPeriod = period)
        }
    }

    fun updateTheme(theme: MusicorumTheme) {
        state.update {
            it.copy(selectedTheme = theme)
        }
    }

    fun updateColCount(count: Int) {
        state.update {
            it.copy(gridColCount = count)
        }
    }

    fun updateRowCount(count: Int) {
        state.update {
            it.copy(gridRowCount = count)
        }
    }

    fun updateStoryMode(value: Boolean) {
        state.update {
            it.copy(storyMode = value)
        }
    }

    fun updateHideUsername(value: Boolean) {
        state.update {
            it.copy(hideUsername = value)
        }
    }

    fun updateSelectedEntity(entity: ResourceEntity) {
        state.update {
            it.copy(selectedEntity = entity)
        }
    }

    fun generateGrid(
        showNames: Boolean
    ) {
        val transaction = Sentry.startTransaction("generate", "task")
        transaction.setTag("entity", state.value.selectedEntity.entityName)
        transaction.setTag("collageType", "grid")
        state.update {
            it.copy(ready = false, isGenerating = true)
        }
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            val url =
                Generator.generateGrid(
                    localUser.username,
                    state.value.gridRowCount,
                    state.value.gridColCount,
                    state.value.selectedEntity.entityName,
                    state.value.selectedPeriod.value.uppercase(Locale.ROOT),
                    showNames
                )
            state.update {
                it.copy(imageUrl = url, ready = true, isGenerating = false)
            }
            if (url == null) {
                transaction.status = SpanStatus.INTERNAL_ERROR
            } else {
                transaction.status = SpanStatus.OK
            }
            transaction.finish()
        }
    }

    fun generateDuotone() {
        val transaction = Sentry.startTransaction("generate", "task")
        transaction.setTag("entity", state.value.selectedEntity.entityName)
        transaction.setTag("collageType", "duotone")
        state.update {
            it.copy(ready = false, isGenerating = true)
        }
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            try {
                val res = Generator.generateDuotone(
                    localUser.username,
                    state.value.selectedEntity.entityName,
                    state.value.selectedPeriod.value,
                    state.value.duotonePalette,
                    state.value.storyMode,
                    state.value.hideUsername
                )
                state.update {
                    it.copy(imageUrl = res)
                }
            } catch (e: Exception) {
                state.update {
                    it.copy(errorMessage = e.message)
                }
            } finally {
                state.update {
                    it.copy(ready = true, isGenerating = false)
                }
            }
        }
    }

    fun downloadFile(): Boolean {
        val manager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = state.value.imageUrl ?: return false

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                return false
            }
        }

        Toast.makeText(ctx, ctx.getString(R.string.starting_download), Toast.LENGTH_SHORT).show()

        val request = DownloadManager.Request(Uri.parse(uri))
            .setMimeType("image/webp")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "chart.webp")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        manager.enqueue(request)
        return true
    }

    fun shareFile() {
        Toast.makeText(ctx, ctx.getString(R.string.sharing_item), Toast.LENGTH_SHORT).show()
        val uri = state.value.imageUrl ?: return
        val tmpFile = File.createTempFile("chart", ".webp", ctx.cacheDir)
        viewModelScope.launch(context = Dispatchers.IO) {
            val stream = URL(uri).openStream()
            tmpFile.writeBytes(stream.readBytes())
        }.invokeOnCompletion {
            val imageUri =
                FileProvider.getUriForFile(ctx, "io.musicorum.mobile.provider", tmpFile)

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                setDataAndType(imageUri, "image/jpeg")
                putExtra(Intent.EXTRA_STREAM, imageUri)
                val text = if (state.value.selectedTheme == MusicorumTheme.GRID) {
                    ctx.getString(
                        R.string.grid_collage_sheet_text,
                        state.value.gridRowCount,
                        state.value.gridColCount
                    )
                } else {
                    ctx.getString(R.string.duotone_collage_sheet_text)
                }
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_TITLE, text)
                flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share Collage")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(shareIntent)
        }
    }

    init {
        savedStateHandle.get<String>("period")?.let { period ->
            state.update {
                it.copy(selectedPeriod = PeriodResolver.resolve(period))
            }
        }
    }
}