package com.aiclk.interad.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
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
        interAdView!!.webViewClient = WebViewClient()
        interAdView!!.loadUrl("http://cdn.aiclicash.com/game/turnplate/turnplate.html?iclicashid=7254985")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        interAdView ?: return super.onKeyDown(keyCode, event)

        if ((keyCode == KeyEvent.KEYCODE_BACK) && interAdView!!.canGoBack()) {
            interAdView!!.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
