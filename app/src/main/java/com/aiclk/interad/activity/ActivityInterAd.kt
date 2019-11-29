package com.aiclk.interad.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import android.webkit.DownloadListener
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aiclk.interad.R


class ActivityInterAd : Activity() {
    val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100
    private var interAdView: WebView? = null
    private var telephonyManager: TelephonyManager? = null

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
            loadWithImei()
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun loadWithImei() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (telephonyManager?.imei != null) {
                interAdView?.loadUrl("https://static.hyrainbow.com/game/turnplate_18/turnplate_18.html?hyid=5050151&redpack=1&back=1&dc=" + telephonyManager?.imei)
                Log.d("tan", "url 1:" + "https://static.hyrainbow.com/game/turnplate_18/turnplate_18.html?hyid=5050151&redpack=1&back=1&dc=" + telephonyManager?.imei)
            } else {
                load()
            }
        } else {
            val deviceId = telephonyManager?.deviceId
            if (deviceId != null) {
                interAdView?.loadUrl("https://static.hyrainbow.com/game/turnplate_18/turnplate_18.html?hyid=5050151&redpack=1&back=1&dc=" + deviceId)
                Log.d("tan", "url 2:" + "http://static.hyrainbow.com/game/turnplate_18/turnplate_18.html?hyid=5050151&redpack=1&back=1&dc=" + deviceId)
            } else {
                load()
            }
        }
    }

    private fun load() {
        interAdView?.loadUrl("https://static.hyrainbow.com/game/turnplate_18/turnplate_18.html?hyid=5050151&redpack=1&back=1&dc=")
        Log.d("tan", "url 3:" + "https://static.hyrainbow.com/game/turnplate_18/turnplate_18.html?hyid=5050151&redpack=1&back=1&dc=")
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
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadWithImei()
        } else {
            load()
        }
    }
}
