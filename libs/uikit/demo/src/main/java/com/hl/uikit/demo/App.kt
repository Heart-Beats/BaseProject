package com.hl.uikit.demo

import android.app.Application
import com.hl.uikit.toastInit

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        toastInit()
    }
}