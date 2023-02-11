package io.musicorum.mobile.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import io.musicorum.mobile.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

fun shareFile(ctx: Context, uri: Uri, text: String? = null) {
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
            }
            sendIntent.flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            ctx.startActivity(Intent.createChooser(sendIntent, null))
        }
    }
}


class CustomProvider : FileProvider(R.xml.file_paths)
