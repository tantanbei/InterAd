package com.hytt.activation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.hytt.activation.R
import com.hytt.activation.content.Const
import com.hytt.activation.trace.Trace
import com.hytt.hyadxopensdk.hyadxopenad.HyAdXOpenMotivateVideoAd
import com.hytt.hyadxopensdk.interfoot.HyAdXOpenListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class ActivityInterAd : Activity() {
    val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100
    private var interAdView: WebView? = null
    private var telephonyManager: TelephonyManager? = null

    private var motevateAd: TextView? = null
    private var hyAdXOpenMotivateVideoAd: HyAdXOpenMotivateVideoAd? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inter_ad)

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        motevateAd = findViewById(R.id.motivate_ad)
        motevateAd?.setOnClickListener(
                View.OnClickListener {
                    hyAdXOpenMotivateVideoAd = HyAdXOpenMotivateVideoAd(this@ActivityInterAd, "7268884", object : HyAdXOpenListener {
                        override fun onAdFill(code: Int, searchId: String, view: View?) {
                            runOnUiThread {
                                Toast.makeText(this@ActivityInterAd, "onAdFill: $searchId", Toast.LENGTH_SHORT).show()
                                Log.d("tan", "onAdFill: ")
                                hyAdXOpenMotivateVideoAd?.show()
                            }
                        }

                        override fun onAdShow(code: Int, searchId: String?) {
                            Toast.makeText(this@ActivityInterAd, "onAdShow: ", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAdClick(code: Int, searchId: String?) {
                            Toast.makeText(this@ActivityInterAd, "onAdClick: ", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAdClose(code: Int, searchId: String?) {
                            Toast.makeText(this@ActivityInterAd, "onAdClose: ", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAdFailed(code: Int, message: String) {
                            Toast.makeText(this@ActivityInterAd, message, Toast.LENGTH_SHORT).show()
                        }

                        override fun onVideoDownloadSuccess(code: Int, searchId: String?) {
                            Toast.makeText(this@ActivityInterAd, "onVideoDownloadSuccess: ", Toast.LENGTH_SHORT).show()
                        }

                        override fun onVideoDownloadFailed(code: Int, searchId: String?) {
                            Toast.makeText(this@ActivityInterAd, "onVideoDownloadFailed: ", Toast.LENGTH_SHORT).show()
                        }

                        override fun onVideoPlayStart(code: Int, searchId: String?) {
                            Toast.makeText(this@ActivityInterAd, "onVideoPlayStart: ", Toast.LENGTH_SHORT).show()
                        }

                        override fun onVideoPlayEnd(code: Int, searchId: String?) {
                            Toast.makeText(this@ActivityInterAd, "onVideoPlayEnd: ", Toast.LENGTH_SHORT).show()
                        }
                    })
                    hyAdXOpenMotivateVideoAd?.load()
                }
        )



        interAdView = findViewById(R.id.inter_ad)
        interAdView?.settings?.javaScriptEnabled = true
        interAdView?.settings?.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            interAdView?.settings?.allowFileAccessFromFileURLs = true
        }
        interAdView?.webViewClient = WebViewClient()
        interAdView?.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            Log.d("tan", "url:" + url + " ua:" + userAgent + " contentDisposition:" + contentDisposition + " minetype:" + mimetype + " contentLength:" + contentLength)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
                return@DownloadListener
            }

            // 指定下载地址
            val request = DownloadManager.Request(Uri.parse(url))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            // 设置下载文件保存的路径和文件名
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
            Log.d("tan", "fileName:" + fileName)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            // 添加一个下载任务
            val downloadId = downloadManager.enqueue(request)
            Trace.SendTrace("download_start", "activation", "", "&opt_uid=" + Const.Uid)
            Log.d("tan", "downloadId:{}" + downloadId)
            Toast.makeText(this, "开始下载" + fileName, Toast.LENGTH_SHORT).show()
        })

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        } else {
            loadWithUid(getUid())
            Trace.SendTrace("openapp", "activation", "", "&opt_uid=" + Const.Uid)

            val list = getPackageList()
            if (list.length > 0) {
                Trace.SendTrace("packagelist", "activation", list, "&opt_uid=" + Const.Uid)
            }
        }
    }

    private fun getPackageList(): String {
        var packageList = ""
        try {
            val process = Runtime.getRuntime().exec("pm list package -3")
            val bis = BufferedReader(InputStreamReader(process.inputStream))
            var line: String? = bis.readLine()
            while (line != null) {
                Log.d("tan", "MainActivity.runCommand, line=" + line)
                if (packageList.length > 0) {
                    packageList += "," + line.split(":").get(1)
                } else {
                    packageList += line.split(":").get(1)
                }
                line = bis.readLine()
            }
        } catch (e: IOException) {
            Log.d("tan", "MainActivity.runCommand,e=" + e)
        }

        return packageList
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getUid(): String {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var imei = telephonyManager?.imei
                if (imei != null) {
                    Const.Uid = imei
                }
                Log.d("tan", "uid 1:" + Const.Uid)
            } else {
                val deviceId = telephonyManager?.deviceId
                if (deviceId != null) {
                    Const.Uid = deviceId
                    Log.d("tan", "uid 2:" + Const.Uid)
                }
            }
        } catch (e: Exception) {
            Log.d("tan", "handle exception:" + e)
        }

        if (Const.Uid.length == 0) {
            Const.Uid = Settings.System.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
        }

        return Const.Uid
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun loadWithUid(uid: String) {
        interAdView?.loadUrl(Const.AppListUrl + uid)
        Log.d("tan", "loadWithUid:" + Const.AppListUrl + uid)
    }

    override fun onBackPressed() {
        interAdView ?: return super.onBackPressed()

        if (interAdView!!.canGoBack()) {
            interAdView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("tan", "requestCode:" + requestCode + " permissions:" + permissions + " grantResults:" + grantResults)
        loadWithUid(getUid())
        Trace.SendTrace("openapp", "activation", "", "&opt_uid=" + Const.Uid)

        val list = getPackageList()
        if (list.length > 0) {
            Trace.SendTrace("packagelist", "activation", list, "&opt_uid=" + Const.Uid)
        }
    }
}
