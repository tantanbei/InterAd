package com.aiclk.interad.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aiclk.interad.R
import android.hardware.usb.UsbDevice.getDeviceId
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log


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
        interAdView!!.settings.javaScriptEnabled = true
        interAdView!!.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            interAdView!!.settings.allowFileAccessFromFileURLs = true
        }
        interAdView!!.webViewClient = WebViewClient()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        } else {
            loadWithImei()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadWithImei() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            interAdView!!.loadUrl("http://cdn.aiclicash.com/game/fuli/fuli.html?iclicashid=7145914&gameTimes=8&IMEI=" + telephonyManager!!.imei)
            Log.d("tan", "url 1:" + "http://cdn.aiclicash.com/game/fuli/fuli.html?iclicashid=7145914&gameTimes=8&IMEI=" + telephonyManager!!.imei)
        } else {
            interAdView!!.loadUrl("http://cdn.aiclicash.com/game/fuli/fuli.html?iclicashid=7145914&gameTimes=8&IMEI=" + telephonyManager!!.deviceId)
            Log.d("tan", "url 2:" + "http://cdn.aiclicash.com/game/fuli/fuli.html?iclicashid=7145914&gameTimes=8&IMEI=" + telephonyManager!!.deviceId)
        }
    }

    private fun load() {
        interAdView!!.loadUrl("http://cdn.aiclicash.com/game/fuli/fuli.html?iclicashid=7145914&gameTimes=8")
        Log.d("tan", "url 3:" + "http://cdn.aiclicash.com/game/fuli/fuli.html?iclicashid=7145914&gameTimes=8")
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
