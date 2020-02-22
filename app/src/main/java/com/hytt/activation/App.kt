package com.hytt.activation

import android.app.Application
import com.hytt.hyadxopensdk.HyAdXOpenSdk

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        HyAdXOpenSdk.getInstance().init(this, "1", 4)
    }
}