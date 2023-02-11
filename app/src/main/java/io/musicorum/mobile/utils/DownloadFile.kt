package io.musicorum.mobile.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

fun downloadFile(ctx: Context, uri: Uri) {
    val manager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    Toast.makeText(ctx, "Starting download...", Toast.LENGTH_SHORT).show()

    val request = DownloadManager.Request(uri)
        .setMimeType("image/webp")
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "chart.webp")
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

    manager.enqueue(request)
}