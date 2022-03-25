package com.hl.uikit.demo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hl.uikit.demo.util.StatusBarUtil

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInsetPadding(top = true)
        StatusBarUtil.transparencyBar(window)
        StatusBarUtil.statusBarLightMode(window)
        StatusBarUtil.setStatusBarColor(this, Color.WHITE)
    }


    protected fun Context.startActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }
}