package com.hytt.activation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import android.webkit.DownloadListener
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hytt.activation.R
import com.hytt.activation.content.Const
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class ActivityInterAd : Activity() {
    val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100
    private var interAdView: WebView? = null
    private var telephonyManager: TelephonyManager? = null
    private var uid: String = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inter_ad)

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        interAdView = findViewById(R.id.inter_ad)
        interAdView?.settings?.javaScriptEnabled = true
        interAdView?.settings?.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            interAdView?.settings?.allowFileAccessFromFileURLs = true
        }
        interAdView?.webViewClient = WebViewClient()
        interAdView?.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        })

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        } else {
            loadWithUid(getUid())
        }

        sendByOKHttp()
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getUid(): String {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var imei = telephonyManager?.imei
                if (imei != null) {
                    uid = imei
                }
                Log.d("tan", "uid 1:" + uid)
            } else {
                val deviceId = telephonyManager?.deviceId
                if (deviceId != null) {
                    uid = deviceId
                    Log.d("tan", "uid 2:" + uid)
                }
            }
        } catch (e: Exception) {
            Log.d("tan", "handle exception:" + e)
        }

        if (uid.length == 0) {
            uid = Settings.System.getString(application.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return uid
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun loadWithUid(uid: String) {
        interAdView?.loadUrl(Const.Url + uid)
        Log.d("tan", "loadWithUid:" + Const.Url + uid)
    }

    private fun load() {
        interAdView?.loadUrl(Const.Url)
        Log.d("tan", "url 3:" + Const.Url)
    }

    override fun onBackPressed() {
        interAdView ?: return super.onBackPressed()

        if (interAdView!!.canGoBack()) {
            interAdView!!.goBack();
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        loadWithUid(getUid())
    }

    private fun sendByOKHttp() {
        Thread(Runnable {
            kotlin.run {
                var conn: HttpURLConnection? = null
                var reader: BufferedReader? = null
                try {
                    val url = URL("https://rcv.hyrainbow.com/trace?t=openapp&op1=activation&opt_uid=" + uid)
                    Log.d("tan", "https://rcv.hyrainbow.com/trace?t=openapp&op1=activation&opt_uid=" + uid)

                    conn = url.openConnection() as HttpURLConnection?
                    //设置请求方法
                    conn?.setRequestMethod("GET")
                    //设置连接超时时间（毫秒）
                    conn?.setConnectTimeout(5000)
                    //设置读取超时时间（毫秒）
                    conn?.setReadTimeout(5000)

                    //返回输入流
                    val input = conn?.getInputStream()

                    //读取输入流
                    reader = BufferedReader(InputStreamReader(input))
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    if (reader != null) {
                        try {
                            reader.close()
                        } catch (e: IOException) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {//关闭连接
                        conn.disconnect()
                    }
                }
            }
        }).start()
    }
}
