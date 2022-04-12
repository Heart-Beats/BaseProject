package com.hl.baseproject

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hl.arch.mvvm.activity.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityMainBinding
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.qrcode.QRScanUtil


class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>() {

    private val qrScanUtil = QRScanUtil(this).apply {
        registerScanActivityResult {
            toast("扫描结果  == $it")
        }
    }

    private val letterArray: List<String> by lazy {
        var list = arrayListOf<String>()
        for (char in 'A'..'C') {
            list.add(char.toString())
        }
        list
    }

    override fun ActivityMainBinding.onViewCreated(savedInstanceState: Bundle?) {
        testScanQrcode.onClick {
            qrScanUtil.launchDefault()
        }


        sideBar.setLetters(letterArray)
    }
}