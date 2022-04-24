package com.hl.baseproject

import android.Manifest
import android.os.Bundle
import com.hl.arch.mvvm.activity.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityMainBinding
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.qrcode.QRScanUtil
import com.hl.utils.reqPermissions


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
    }

}