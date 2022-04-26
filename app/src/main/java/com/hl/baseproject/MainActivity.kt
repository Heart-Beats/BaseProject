package com.hl.baseproject

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import com.hl.arch.mvvm.activity.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityMainBinding
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.qrcode.QRScanUtil
import com.hl.utils.reqPermissions
import com.hl.utils.setImmersiveSystemBar


class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>() {


    private val qrScanUtil = QRScanUtil(this).apply {
        registerScanActivityResult {
            toast("扫描结果  == $it")
        }
    }

    private val letterArray: List<String> by lazy {
        val list = arrayListOf<String>()
        for (char in 'A'..'C') {
            list.add(char.toString())
        }
        list
    }

    override fun ActivityMainBinding.onViewCreated(savedInstanceState: Bundle?) {
        uikitToolbar.title = "主页面"

        testScanQrcode.onClick {

            reqPermissions(Manifest.permission.CAMERA, deniedAction = {
                toast("你拒绝了权限请求呀")

            }) {
                qrScanUtil.launchDefault()
            }
        }

        sideBar.setLetters(letterArray)


        test.onClick {
            requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setImmersiveSystemBar(true)
        } else {
            setImmersiveSystemBar(false)
        }
    }
}