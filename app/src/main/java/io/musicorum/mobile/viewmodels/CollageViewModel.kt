package io.musicorum.mobile.viewmodels

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.R
import io.musicorum.mobile.ktor.endpoints.musicorum.generator.Generator
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.MusicorumTheme
import io.musicorum.mobile.models.ResourceEntity
import io.musicorum.mobile.repositories.LocalUserRepository
import io.sentry.Sentry
import io.sentry.SpanStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL
import java.util.Locale

class CollageViewModel(application: Application) : AndroidViewModel(application) {
    val imageUrl: MutableLiveData<String> = MutableLiveData()
    val ready = MutableLiveData(false)
    val isGenerating = MutableLiveData(false)
    val errorMessage = MutableLiveData("")
    val ctx = application as Context

    val selectedTheme = MutableLiveData(MusicorumTheme.GRID)
    val selectedPeriod = MutableLiveData(FetchPeriod.WEEK)
    val selectedEntity = MutableLiveData(ResourceEntity.Album)
    val hideUsername = MutableLiveData(false)
    val storyMode = MutableLiveData(true)
    val gridRowCount = MutableLiveData(6)
    val gridColCount = MutableLiveData(6)
    val duotonePalette = MutableLiveData("Purplish")

    fun generateGrid(
        showNames: Boolean
    ) {
        val transaction = Sentry.startTransaction("generate", "task")
        transaction.setTag("entity", selectedEntity.value!!.entityName)
        transaction.setTag("collageType", "grid")
        ready.value = false
        isGenerating.value = true
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            val url =
                Generator.generateGrid(
                    localUser.username,
                    gridRowCount.value!!,
                    gridColCount.value!!,
                    selectedEntity.value!!.entityName,
                    selectedPeriod.value!!.value.uppercase(Locale.ROOT),
                    showNames
                )
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

    fun generateDuotone() {
        val transaction = Sentry.startTransaction("generate", "task")
        transaction.setTag("entity", selectedEntity.value?.entityName ?: "????")
        transaction.setTag("collageType", "duotone")
        ready.value = false
        isGenerating.value = true
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            try {
                val res = Generator.generateDuotone(
                    localUser.username,
                    selectedEntity.value!!.entityName,
                    selectedPeriod.value!!.value,
                    duotonePalette.value!!,
                    storyMode.value!!,
                    hideUsername.value!!
                )
                imageUrl.value = res
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                ready.value = true
                isGenerating.value = false
            }
        }
    }

    fun downloadFile() {
        val manager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = imageUrl.value ?: return
        Toast.makeText(ctx, ctx.getString(R.string.starting_download), Toast.LENGTH_SHORT).show()

        val request = DownloadManager.Request(Uri.parse(uri))
            .setMimeType("image/webp")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "chart.webp")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        manager.enqueue(request)
    }

    fun shareFile() {
        Toast.makeText(ctx, ctx.getString(R.string.sharing_item), Toast.LENGTH_SHORT).show()
        val uri = imageUrl.value ?: return
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
                val text = if (selectedTheme.value == MusicorumTheme.GRID) {
                    ctx.getString(
                        R.string.grid_collage_sheet_text,
                        gridRowCount.value!!,
                        gridColCount.value!!
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
    }
}