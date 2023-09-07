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
import io.musicorum.mobile.ktor.endpoints.musicorum.Generator
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.repositories.LocalUserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class ChartCollageViewModel(application: Application) : AndroidViewModel(application) {
    val imageUrl: MutableLiveData<String> = MutableLiveData()
    val ready = MutableLiveData(false)
    val isGenerating = MutableLiveData(false)
    val user = MutableLiveData<PartialUser>(null)
    val ctx = application as Context

    fun generate(
        username: String,
        rowCount: Int,
        colCount: Int,
        entity: String,
        period: String,
        showNames: Boolean
    ) {
        //val transaction = Sentry.startTransaction("generate", "task")
        //transaction.setTag("entity", entity)
        //transaction.setTag("collageType", "grid")
        ready.value = false
        isGenerating.value = true
        viewModelScope.launch {
            val url =
                Generator.generateGrid(username, rowCount, colCount, entity, period, showNames)
            imageUrl.value = url
            ready.value = true
            isGenerating.value = false
            if (url == null) {
                //transaction.status = SpanStatus.INTERNAL_ERROR
            } else {
                //transaction.status = SpanStatus.OK
            }
            //transaction.finish()
        }
    }

    private fun init() {
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            user.value = localUser
        }
    }

    fun downloadFile(uri: Uri) {
        val manager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        Toast.makeText(ctx, "Starting download...", Toast.LENGTH_SHORT).show()

        val request = DownloadManager.Request(uri)
            .setMimeType("image/webp")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "chart.webp")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        manager.enqueue(request)
    }

    fun shareFile(uri: Uri, text: String? = null) {
        Toast.makeText(ctx, "Sharing item...", Toast.LENGTH_SHORT).show()

        val tmpFile = File.createTempFile("chart", ".webp", ctx.cacheDir)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val stream = URL(uri.toString()).openStream()
                tmpFile.writeBytes(stream.readBytes())
                val imageUri =
                    FileProvider.getUriForFile(ctx, "io.musicorum.mobile.provider", tmpFile)


                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                }

                sendIntent.setDataAndType(imageUri, "image/jpeg")
                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                //sendIntent.clipData = ClipData.newRawUri("", imageUri)
                text?.let {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, it)
                    sendIntent.putExtra(Intent.EXTRA_TITLE, it)
                }
                sendIntent.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                ctx.startActivity(Intent.createChooser(sendIntent, "Share Collage"))
            }
        }
    }

    init {
        init()
    }
}