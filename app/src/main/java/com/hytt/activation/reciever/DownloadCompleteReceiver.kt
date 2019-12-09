package com.hytt.activation.reciever

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.hytt.activation.content.Const
import com.hytt.activation.trace.Trace


class DownloadCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("tan", "onReceive: intent:$intent")
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
            Trace.SendTrace("download_complete", "activation", "", "&opt_uid=" + Const.Uid)

            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.d("tan", "downloadId: $downloadId")
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            var type = downloadManager.getMimeTypeForDownloadedFile(downloadId)
            Log.d("tan", "getMimeTypeForDownloadedFile:{}$type")
            if (TextUtils.isEmpty(type)) {
                type = "*/*"
            }
            val uri = downloadManager.getUriForDownloadedFile(downloadId)
            Log.d("tan", "UriForDownloadedFile: $uri")
            if (uri != null) {
                Trace.SendTrace("install_start", "activation", "", "&opt_uid=" + Const.Uid)
                val handlerIntent = Intent()
                handlerIntent.setDataAndType(uri, type)
                val mimeType = "application/vnd.android.package-archive"
                handlerIntent.action = Intent.ACTION_INSTALL_PACKAGE // Intent Action
                handlerIntent.setDataAndType(uri, mimeType) // 设置文件的Uri和Mime Type
                handlerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 在新栈启动Activity
                handlerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 允许三方应用读文件
                context.startActivity(handlerIntent)
            }
        }
    }
}