package com.aiclk.interad.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aiclk.interad.R

class ActivityInterAd : Activity() {
    private var interAdView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inter_ad)

        interAdView = findViewById(R.id.inter_ad)
        interAdView!!.settings.javaScriptEnabled = true
        interAdView!!.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            interAdView!!.settings.allowFileAccessFromFileURLs = true
        }
        interAdView!!.webViewClient = WebViewClient()
        interAdView!!.loadUrl("http://cdn.aiclicash.com/game/fuli/fuli.html?iclicashid=7145914")
    }

    override fun onBackPressed() {
        interAdView ?: return super.onBackPressed()

        if (interAdView!!.canGoBack()) {
            interAdView!!.goBack();
        } else {
            super.onBackPressed()
        }
    }
}
